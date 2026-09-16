package com.fatedice.client;

import com.fatedice.init.ModEffects;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

public class ClientControlEvents {

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        if (player == null) return;

        Input input = event.getInput();

        // 1. Inverted Controls (WASD reversed)
        if (player.hasEffect(ModEffects.INVERTED_CONTROLS)) {
            input.forwardImpulse *= -1;
            input.leftImpulse *= -1;

            boolean tempUp = input.up;
            input.up = input.down;
            input.down = tempUp;

            boolean tempLeft = input.left;
            input.left = input.right;
            input.right = tempLeft;
        }

        // 2. Forced Sprint (Cannot stop moving forward)
        if (player.hasEffect(ModEffects.FORCED_SPRINT)) {
            input.forwardImpulse = 1.0F;
            input.up = true;
            input.down = false;
        }

        // 3. Heavy Legs (Cannot jump)
        if (player.hasEffect(ModEffects.HEAVY_LEGS)) {
            input.jumping = false;
        }
    }
}
