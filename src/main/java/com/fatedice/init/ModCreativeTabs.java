package com.fatedice.init;

import com.fatedice.FateDiceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FateDiceMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FATE_DICE_TAB =
            CREATIVE_MODE_TABS.register("fate_dice_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.fatedice.fate_dice_tab"))
                    .icon(() -> new ItemStack(ModItems.D20_DICE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.D20_DICE.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
