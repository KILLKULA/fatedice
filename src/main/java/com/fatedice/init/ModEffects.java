package com.fatedice.init;

import com.fatedice.FateDiceMod;
import com.fatedice.effect.InvertedControlsEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, FateDiceMod.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> INVERTED_CONTROLS =
            MOB_EFFECTS.register("inverted_controls",
                    () -> new InvertedControlsEffect(MobEffectCategory.HARMFUL, 0x990033));

    public static final DeferredHolder<MobEffect, MobEffect> FORCED_SPRINT =
            MOB_EFFECTS.register("forced_sprint",
                    () -> new InvertedControlsEffect(MobEffectCategory.HARMFUL, 0xFF6600));

    public static final DeferredHolder<MobEffect, MobEffect> HEAVY_LEGS =
            MOB_EFFECTS.register("heavy_legs",
                    () -> new InvertedControlsEffect(MobEffectCategory.HARMFUL, 0x555555));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
