package net.netherway.starwarschaincode.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WeaponReloadState {

    private static final Map<UUID, Set<InteractionHand>> reloadingHands = new HashMap<>();

    public static boolean isReloading(ServerPlayer player, InteractionHand hand) {
        return reloadingHands.getOrDefault(player.getUUID(), Set.of()).contains(hand);
    }

    public static void setReloading(ServerPlayer player, InteractionHand hand, boolean reloading) {
        Set<InteractionHand> set = reloadingHands.computeIfAbsent(player.getUUID(), k -> new HashSet<>());
        if (reloading) set.add(hand); else set.remove(hand);
    }
}