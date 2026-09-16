package com.fatedice;

import com.fatedice.client.ClientControlEvents;
import com.fatedice.config.FateConfig;
import com.fatedice.fate.FateEventsPool;
import com.fatedice.fate.FateScheduler;
import com.fatedice.init.ModCreativeTabs;
import com.fatedice.init.ModEffects;
import com.fatedice.init.ModItems;
import com.fatedice.init.ModSounds;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(FateDiceMod.MOD_ID)
public class FateDiceMod {
    public static final String MOD_ID = "fatedice";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FateDiceMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing Fate Dice (Бросок судьбы) with Expanded Event Matrix for NeoForge 1.21.1...");

        // Register Deferred Registers to Mod Event Bus
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        // Register Config
        modContainer.registerConfig(ModConfig.Type.COMMON, FateConfig.SPEC);

        // Register Event Pools (100+ outcomes)
        FateEventsPool.init();

        // Register Server Tick Handler
        NeoForge.EVENT_BUS.register(FateScheduler.class);

        // Register Client Controls Handler on client distribution
        if (FMLLoader.getDist().isClient()) {
            NeoForge.EVENT_BUS.register(ClientControlEvents.class);
        }

        LOGGER.info("Fate Dice loaded successfully! 100+ Destiny outcomes ready.");
    }
}
