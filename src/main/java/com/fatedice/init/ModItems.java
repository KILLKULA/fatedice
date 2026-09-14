package com.fatedice.init;

import com.fatedice.FateDiceMod;
import com.fatedice.item.FateDiceItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FateDiceMod.MOD_ID);

    public static final DeferredItem<FateDiceItem> D20_DICE = ITEMS.registerItem(
            "d20_dice",
            properties -> new FateDiceItem(properties.stacksTo(1).rarity(Rarity.EPIC).fireResistant())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
