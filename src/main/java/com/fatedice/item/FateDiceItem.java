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
        tooltipComponents.add(Component.translatable("tooltip.fatedice.d20_dice.instruction"));
        tooltipComponents.add(Component.translatable("tooltip.fatedice.d20_dice.lore"));
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.translatable("tooltip.fatedice.d20_dice.tier_20"));
        tooltipComponents.add(Component.translatable("tooltip.fatedice.d20_dice.tier_16_19"));
        tooltipComponents.add(Component.translatable("tooltip.fatedice.d20_dice.tier_11_15"));
        tooltipComponents.add(Component.translatable("tooltip.fatedice.d20_dice.tier_10"));
        tooltipComponents.add(Component.translatable("tooltip.fatedice.d20_dice.tier_2_9"));
        tooltipComponents.add(Component.translatable("tooltip.fatedice.d20_dice.tier_1"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
