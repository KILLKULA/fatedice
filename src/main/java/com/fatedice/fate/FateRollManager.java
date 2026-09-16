package com.fatedice.fate;

import com.fatedice.config.FateConfig;
import com.fatedice.init.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public class FateRollManager {

    public static void startRoll(ServerPlayer player, ItemStack diceStack) {
        // Apply cooldown
        int cooldownTicks = FateConfig.COOLDOWN_SECONDS.get() * 20;
        player.getCooldowns().addCooldown(diceStack.getItem(), cooldownTicks);

        ServerLevel level = player.serverLevel();
        level.playSound(null, player.blockPosition(), SoundEvents.BONE_BLOCK_PLACE, SoundSource.PLAYERS, 1.0F, 1.2F);

        int suspenseTicks = FateConfig.SUSPENSE_DELAY_TICKS.get();
        if (suspenseTicks > 0) {
            player.displayClientMessage(Component.translatable("message.fatedice.rolling"), true);
            FateScheduler.schedule(suspenseTicks / 2, () -> {
                level.playSound(null, player.blockPosition(), SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.PLAYERS, 1.0F, 1.4F);
            });
            FateScheduler.schedule(suspenseTicks, () -> revealRoll(player));
        } else {
            revealRoll(player);
        }
    }

    private static void revealRoll(ServerPlayer player) {
        if (!player.isAlive()) return;

        ServerLevel level = player.serverLevel();
        int roll = level.getRandom().nextInt(20) + 1;
        FateTier tier = FateEventsPool.getTier(roll);

        if (tier == null) return;

        String color = tier.getColorCode();
        Component category = tier.getCategoryName();
        Component title = tier.getTierTitle();

        // Screen titles and sounds based on tier
        if (roll == 1) {
            // CRITICAL FAIL
            sendTitle(player, Component.translatable("title.fatedice.critical_fail"), Component.translatable("subtitle.fatedice.critical_fail"));
            level.playSound(null, player.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.0F, 0.6F);
            level.playSound(null, player.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 0.8F);
            level.sendParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + 1.0, player.getZ(), 40, 0.5, 0.5, 0.5, 0.05);
        } else if (roll == 20) {
            // NATURAL 20
            sendTitle(player, Component.translatable("title.fatedice.natural_20"), Component.translatable("subtitle.fatedice.natural_20"));
            level.broadcastEntityEvent(player, (byte) 35); // Totem of Undying animation
            level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            level.playSound(null, player.blockPosition(), SoundEvents.RAID_HORN.value(), SoundSource.PLAYERS, 1.2F, 1.0F);
            level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 60, 0.5, 0.5, 0.5, 0.2);
            level.sendParticles(ParticleTypes.FIREWORK, player.getX(), player.getY() + 1.5, player.getZ(), 50, 0.6, 0.8, 0.6, 0.15);
        } else if (roll == 10) {
            // NEUTRAL
            sendTitle(player, Component.translatable("title.fatedice.neutral"), Component.translatable("subtitle.fatedice.neutral"));
            level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else if (roll <= 9) {
            // MISFORTUNE
            sendTitle(player, Component.translatable("title.fatedice.misfortune", color, roll), Component.translatable("subtitle.fatedice.misfortune"));
            level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.PLAYERS, 1.0F, 0.6F);
        } else if (roll <= 15) {
            // MINOR BONUS / BONUS
            sendTitle(player, Component.translatable("title.fatedice.bonus", color, roll), Component.translatable("subtitle.fatedice.bonus"));
            level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.2F);
        } else {
            // VERY GOOD BONUS (16-19)
            sendTitle(player, Component.translatable("title.fatedice.great_bonus", color, roll), Component.translatable("subtitle.fatedice.great_bonus"));
            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.1F);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.6, 0.6, 0.6, 0.1);
        }

        // Actionbar notification
        player.displayClientMessage(Component.translatable("message.fatedice.actionbar", color, roll, title), true);

        // Chat notification
        if (FateConfig.BROADCAST_ROLL_TO_CHAT.get()) {
            Component chatMessage = Component.translatable("message.fatedice.chat", player.getName().getString(), color, roll, title);
            level.getServer().getPlayerList().broadcastSystemMessage(chatMessage, false);
        }

        // Execute chosen sub-event
        tier.trigger(level, player, player.blockPosition());
    }

    private static void sendTitle(ServerPlayer player, Component title, Component subtitle) {
        player.connection.send(new ClientboundSetTitlesAnimationPacket(5, 45, 15));
        player.connection.send(new ClientboundSetTitleTextPacket(title));
        player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
    }
}
