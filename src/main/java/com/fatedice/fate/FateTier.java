package com.fatedice.fate;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class FateTier {
    private final int rollValue;
    private final Component tierTitle;
    private final Component categoryName;
    private final String colorCode;
    private final List<FateSubEvent> subEvents = new ArrayList<>();

    public FateTier(int rollValue, Component tierTitle, Component categoryName, String colorCode) {
        this.rollValue = rollValue;
        this.tierTitle = tierTitle;
        this.categoryName = categoryName;
        this.colorCode = colorCode;
    }

    public FateTier add(FateSubEvent event) {
        this.subEvents.add(event);
        return this;
    }

    public int getRollValue() {
        return rollValue;
    }

    public Component getTierTitle() {
        return tierTitle;
    }

    public Component getCategoryName() {
        return categoryName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public List<FateSubEvent> getSubEvents() {
        return subEvents;
    }

    public void trigger(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (subEvents.isEmpty()) return;
        RandomSource random = level.getRandom();
        FateSubEvent chosen = subEvents.get(random.nextInt(subEvents.size()));
        chosen.execute(level, player, pos);
    }
}
