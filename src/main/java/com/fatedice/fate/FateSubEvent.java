package com.fatedice.fate;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface FateSubEvent {
    void execute(ServerLevel level, ServerPlayer player, BlockPos pos);

    default Component getSubName() {
        return Component.empty();
    }
}
