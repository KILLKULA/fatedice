package com.fatedice.fate;

import com.fatedice.config.FateConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

public class FateMobHelper {
    public static final String TAG_BOSS_COLOSSUS = "fate_boss_colossus";
    public static final String TAG_BOSS_SNIPER = "fate_boss_sniper";
    public static final String TAG_BOSS_MAGMA = "fate_boss_magma";
    public static final String TAG_BOSS_LEVIATHAN = "fate_boss_leviathan";
    public static final String TAG_BOSS_VINDICATOR = "fate_boss_vindicator";
    public static final String TAG_NO_GRIEF = "fate_no_grief";

    public static void scaleAndBuff(Mob mob, double scale, double maxHealth, double attackDamage, double speedMult, double knockbackRes) {
        AttributeInstance scaleAttr = mob.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(scale);
        }

        if (maxHealth > 0) {
            AttributeInstance hpAttr = mob.getAttribute(Attributes.MAX_HEALTH);
            if (hpAttr != null) {
                hpAttr.setBaseValue(maxHealth);
                mob.setHealth((float) maxHealth);
            }
        }

        if (attackDamage > 0) {
            AttributeInstance dmgAttr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
            if (dmgAttr != null) {
                dmgAttr.setBaseValue(attackDamage);
            }
        }

        if (speedMult > 0) {
            AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttr != null) {
                speedAttr.setBaseValue(speedAttr.getBaseValue() * speedMult);
            }
        }

        if (knockbackRes > 0) {
            AttributeInstance kbAttr = mob.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (kbAttr != null) {
                kbAttr.setBaseValue(knockbackRes);
            }
        }

        // Prevent despawning naturally so the player doesn't lose the encounter/loot
        mob.setPersistenceRequired();
    }

    public static void equip(Mob mob, ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet, ItemStack mainHand, ItemStack offHand) {
        if (!head.isEmpty()) {
            mob.setItemSlot(EquipmentSlot.HEAD, head);
            mob.setDropChance(EquipmentSlot.HEAD, 0.25F);
        }
        if (!chest.isEmpty()) {
            mob.setItemSlot(EquipmentSlot.CHEST, chest);
            mob.setDropChance(EquipmentSlot.CHEST, 0.25F);
        }
        if (!legs.isEmpty()) {
            mob.setItemSlot(EquipmentSlot.LEGS, legs);
            mob.setDropChance(EquipmentSlot.LEGS, 0.25F);
        }
        if (!feet.isEmpty()) {
            mob.setItemSlot(EquipmentSlot.FEET, feet);
            mob.setDropChance(EquipmentSlot.FEET, 0.25F);
        }
        if (!mainHand.isEmpty()) {
            mob.setItemSlot(EquipmentSlot.MAINHAND, mainHand);
            mob.setDropChance(EquipmentSlot.MAINHAND, 0.5F);
        }
        if (!offHand.isEmpty()) {
            mob.setItemSlot(EquipmentSlot.OFFHAND, offHand);
            mob.setDropChance(EquipmentSlot.OFFHAND, 0.5F);
        }
    }

    public static void setupTitanCreeper(Creeper creeper, double scale, int explosionRadius, boolean charged) {
        scaleAndBuff(creeper, scale, 60.0, 0, 1.2, 0.7);

        boolean allowDestructive = FateConfig.ALLOW_DESTRUCTIVE_EVENTS.get();
        int finalRadius = allowDestructive ? explosionRadius : 3;

        CompoundTag tag = new CompoundTag();
        creeper.addAdditionalSaveData(tag);
        tag.putByte("ExplosionRadius", (byte) finalRadius);
        if (charged) {
            tag.putBoolean("powered", true);
        }
        creeper.readAdditionalSaveData(tag);

        if (!allowDestructive) {
            creeper.addTag(TAG_NO_GRIEF);
        }
    }

    public static void dropItem(ServerLevel level, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) return;
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, stack);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel level) {
            BlockPos pos = entity.blockPosition();

            if (entity.getTags().contains(TAG_BOSS_COLOSSUS)) {
                // Drop 3-5 Diamonds
                dropItem(level, pos, new ItemStack(Items.DIAMOND, 3 + level.random.nextInt(3)));
                // Drop 1-2 Golden Apples
                dropItem(level, pos, new ItemStack(Items.GOLDEN_APPLE, 1 + level.random.nextInt(2)));
                // Drop 8-16 Emeralds
                dropItem(level, pos, new ItemStack(Items.EMERALD, 8 + level.random.nextInt(9)));
                // Drop 1 Netherite Scrap
                dropItem(level, pos, new ItemStack(Items.NETHERITE_SCRAP, 1));

                // Enchanted Netherite Axe
                ItemStack axe = new ItemStack(Items.NETHERITE_AXE);
                var enchReg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
                enchReg.getHolder(Enchantments.SHARPNESS).ifPresent(h -> axe.enchant(h, 5));
                enchReg.getHolder(Enchantments.UNBREAKING).ifPresent(h -> axe.enchant(h, 3));
                enchReg.getHolder(Enchantments.EFFICIENCY).ifPresent(h -> axe.enchant(h, 4));
                dropItem(level, pos, axe);

                broadcastBossDefeat(level, pos, "§6§l[Судьба] Зомби-Колосс повержен! Выпали алмазы, золотые яблоки и незеритовый топор!");
            }
            else if (entity.getTags().contains(TAG_BOSS_SNIPER)) {
                // Drop 2-4 Diamonds
                dropItem(level, pos, new ItemStack(Items.DIAMOND, 2 + level.random.nextInt(3)));
                // Drop 1 Golden Apple
                dropItem(level, pos, new ItemStack(Items.GOLDEN_APPLE, 1));
                // Drop 32 Spectral Arrows
                dropItem(level, pos, new ItemStack(Items.SPECTRAL_ARROW, 32));
                // Drop 8-16 Emeralds
                dropItem(level, pos, new ItemStack(Items.EMERALD, 8 + level.random.nextInt(9)));

                // Enchanted Bow
                ItemStack bow = new ItemStack(Items.BOW);
                var enchReg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
                enchReg.getHolder(Enchantments.POWER).ifPresent(h -> bow.enchant(h, 5));
                enchReg.getHolder(Enchantments.PUNCH).ifPresent(h -> bow.enchant(h, 2));
                enchReg.getHolder(Enchantments.UNBREAKING).ifPresent(h -> bow.enchant(h, 3));
                enchReg.getHolder(Enchantments.FLAME).ifPresent(h -> bow.enchant(h, 1));
                dropItem(level, pos, bow);

                broadcastBossDefeat(level, pos, "§6§l[Судьба] Колоссальный Скелет-Снайпер уничтожен! Выпал легендарный лук и алмазы!");
            }
            else if (entity.getTags().contains(TAG_BOSS_MAGMA)) {
                // Drop 2-4 Diamonds
                dropItem(level, pos, new ItemStack(Items.DIAMOND, 2 + level.random.nextInt(3)));
                // Drop 1 Golden Apple
                dropItem(level, pos, new ItemStack(Items.GOLDEN_APPLE, 1));
                // Drop 8-16 Gold Ingots
                dropItem(level, pos, new ItemStack(Items.GOLD_INGOT, 8 + level.random.nextInt(9)));
                // Drop 4-8 Magma Creams
                dropItem(level, pos, new ItemStack(Items.MAGMA_CREAM, 4 + level.random.nextInt(5)));
                // Drop Netherite Ingot
                dropItem(level, pos, new ItemStack(Items.NETHERITE_INGOT, 1));

                broadcastBossDefeat(level, pos, "§6§l[Судьба] Инфернальный Магма-Титан расколот! Раскаленный незерит и золото ваши!");
            }
            else if (entity.getTags().contains(TAG_BOSS_LEVIATHAN)) {
                // Drop 2-3 Diamonds
                dropItem(level, pos, new ItemStack(Items.DIAMOND, 2 + level.random.nextInt(2)));
                // Drop 1 Enchanted Golden Apple
                dropItem(level, pos, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1));
                // Drop 6-12 Phantom Membranes
                dropItem(level, pos, new ItemStack(Items.PHANTOM_MEMBRANE, 6 + level.random.nextInt(7)));
                // Drop Elytra
                dropItem(level, pos, new ItemStack(Items.ELYTRA, 1));

                broadcastBossDefeat(level, pos, "§6§l[Судьба] Громовой Фантом-Левиафан низвергнут! Получены элитры и зачарованное яблоко!");
            }
            else if (entity.getTags().contains(TAG_BOSS_VINDICATOR)) {
                // Drop 1-3 Diamonds
                dropItem(level, pos, new ItemStack(Items.DIAMOND, 1 + level.random.nextInt(3)));
                // Drop 1 Totem of Undying
                dropItem(level, pos, new ItemStack(Items.TOTEM_OF_UNDYING, 1));
                // Drop 12-24 Emeralds
                dropItem(level, pos, new ItemStack(Items.EMERALD, 12 + level.random.nextInt(13)));

                broadcastBossDefeat(level, pos, "§6§l[Судьба] Разбойник-Громила повержен! Тотем бессмертия и изумруды получены!");
            }
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!FateConfig.ALLOW_DESTRUCTIVE_EVENTS.get()) {
            LivingEntity direct = event.getExplosion().getIndirectSourceEntity() instanceof LivingEntity le ? le : null;
            if (direct != null && direct.getTags().contains(TAG_NO_GRIEF)) {
                event.getAffectedBlocks().clear();
            }
        }
    }

    private static void broadcastBossDefeat(ServerLevel level, BlockPos pos, String message) {
        level.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.2F, 1.0F);
        level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 40, 0.6, 0.6, 0.6, 0.15);
        level.players().forEach(p -> {
            if (p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 150.0 * 150.0) {
                p.displayClientMessage(Component.literal(message), false);
            }
        });
    }
}
