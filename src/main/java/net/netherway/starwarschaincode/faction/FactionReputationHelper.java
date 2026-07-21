package net.netherway.starwarschaincode.faction;

import net.minecraft.server.level.ServerPlayer;
import net.netherway.starwarschaincode.component.ModAttachments;

public class FactionReputationHelper {

    public static int getFactionReputation(ServerPlayer player, Faction faction) {
        return player.getData(ModAttachments.FACTION_REPUTATION).get(faction);
    }

    public static void increaseFactionReputation(ServerPlayer player, Faction faction, int amount) {
        setReputation(player, faction, getFactionReputation(player, faction) + amount);
    }

    public static void decreaseFactionReputation(ServerPlayer player, Faction faction, int amount) {
        setReputation(player, faction, getFactionReputation(player, faction) - amount);
    }

    public static void setReputation(ServerPlayer player, Faction faction, int value) {
        FactionReputationData current = player.getData(ModAttachments.FACTION_REPUTATION);
        player.setData(ModAttachments.FACTION_REPUTATION, current.with(faction, value));
    }

    public static boolean isHostile(ServerPlayer player, Faction faction) {
        return getFactionReputation(player, faction) <= faction.getHostileThreshold();
    }

    public static boolean isFriendly(ServerPlayer player, Faction faction) {
        return getFactionReputation(player, faction) >= faction.getFriendlyThreshold();
    }
}