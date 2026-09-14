package com.fatedice.item;

import com.fatedice.fate.FateRollManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FateDiceItem extends Item {

    public FateDiceItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            FateRollManager.startRoll(serverPlayer, stack);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§7Нажмите §e[ПКМ]§7, чтобы бросить кубик судьбы."));
        tooltipComponents.add(Component.literal("§8«Фортуна благоволит смелым... или карает их.»"));
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.literal("§6★ 20: §eБлагословение Богов"));
        tooltipComponents.add(Component.literal("§b16–19: §fОтличные бонусы и руды"));
        tooltipComponents.add(Component.literal("§a11–15: §fПолезные усиления"));
        tooltipComponents.add(Component.literal("§e10: §7Судьба молчит"));
        tooltipComponents.add(Component.literal("§c2–9: §fНеприятности и испытания"));
        tooltipComponents.add(Component.literal("§4💀 1: §cГнев судьбы (Критический провал)"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
