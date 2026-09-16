package com.fatedice.fate;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FatePlayerUtils {
    public static final double DEFAULT_RADIUS = 150.0;

    /**
     * Finds the nearest other alive player within the specified radius.
     */
    public static ServerPlayer findNearestOtherPlayer(ServerLevel level, ServerPlayer sourcePlayer, double maxRadius) {
        ServerPlayer nearest = null;
        double minDistanceSq = maxRadius * maxRadius;

        for (ServerPlayer other : level.players()) {
            if (other != sourcePlayer && other.isAlive() && !other.isSpectator()) {
                double distSq = other.distanceToSqr(sourcePlayer);
                if (distSq <= minDistanceSq) {
                    minDistanceSq = distSq;
                    nearest = other;
                }
            }
        }
        return nearest;
    }

    /**
     * Finds all other alive players within the radius (for AoE buffs / party effects).
     */
    public static List<ServerPlayer> findOtherPlayersInRadius(ServerLevel level, ServerPlayer sourcePlayer, double maxRadius) {
        double radiusSq = maxRadius * maxRadius;
        return level.players().stream()
                .filter(p -> p != sourcePlayer && p.isAlive() && !p.isSpectator() && p.distanceToSqr(sourcePlayer) <= radiusSq)
                .toList();
    }

    /**
     * Fallback for singleplayer: finds nearest living entity (mob/animal) within radius.
     */
    public static LivingEntity findNearestOtherEntity(ServerLevel level, ServerPlayer sourcePlayer, double maxRadius) {
        AABB box = sourcePlayer.getBoundingBox().inflate(maxRadius);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != sourcePlayer && e.isAlive());
        LivingEntity nearest = null;
        double minDistanceSq = maxRadius * maxRadius;

        for (LivingEntity entity : entities) {
            double distSq = entity.distanceToSqr(sourcePlayer);
            if (distSq <= minDistanceSq) {
                minDistanceSq = distSq;
                nearest = entity;
            }
        }
        return nearest;
    }
}
