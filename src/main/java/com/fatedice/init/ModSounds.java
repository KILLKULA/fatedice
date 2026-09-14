package com.fatedice.init;

import com.fatedice.FateDiceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, FateDiceMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> DICE_ROLL = registerSoundEvent("dice_roll");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRITICAL_SUCCESS = registerSoundEvent("critical_success");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRITICAL_FAIL = registerSoundEvent("critical_fail");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FateDiceMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
