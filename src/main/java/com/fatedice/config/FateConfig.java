package com.fatedice.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class FateConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue COOLDOWN_SECONDS;
    public static final ModConfigSpec.BooleanValue ALLOW_DESTRUCTIVE_EVENTS;
    public static final ModConfigSpec.BooleanValue BROADCAST_ROLL_TO_CHAT;
    public static final ModConfigSpec.IntValue SUSPENSE_DELAY_TICKS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("general");

        COOLDOWN_SECONDS = builder
                .comment("Cooldown duration for D20 Dice in seconds (Default: 20)")
                .translation("fatedice.configuration.cooldownSeconds")
                .defineInRange("cooldownSeconds", 20, 1, 3600);

        ALLOW_DESTRUCTIVE_EVENTS = builder
                .comment("Allow potentially destructive events like fires, explosions, or lightning that can damage blocks (Default: true)")
                .translation("fatedice.configuration.allowDestructiveEvents")
                .define("allowDestructiveEvents", true);

        BROADCAST_ROLL_TO_CHAT = builder
                .comment("Broadcast the roll outcome and narrative message to all nearby players in chat (Default: true)")
                .translation("fatedice.configuration.broadcastRollToChat")
                .define("broadcastRollToChat", true);

        SUSPENSE_DELAY_TICKS = builder
                .comment("Delay in ticks before the final dice roll is revealed, playing clatter sounds (Default: 25 ticks = 1.25s)")
                .translation("fatedice.configuration.suspenseDelayTicks")
                .defineInRange("suspenseDelayTicks", 25, 0, 100);

        builder.pop();
        SPEC = builder.build();
    }
}
