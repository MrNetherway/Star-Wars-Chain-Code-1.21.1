package net.netherway.starwarschaincode.faction;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum Faction implements StringRepresentable {
    EMPIRE("empire", "Império Galáctico", 50, 1, 99),
    REBELLION("rebellion", "Aliança Rebelde", 0, -50, 50),
    HUTT_CARTEL("hutt_cartel", "Cartel Hutt", 0, -50, 50);

    public static final Codec<Faction> CODEC = StringRepresentable.fromEnum(Faction::values);

    private final String id;
    private final String displayName;
    private final int startingReputation;
    private final int hostileThreshold;
    private final int friendlyThreshold;

    Faction(String id, String displayName, int startingReputation, int hostileThreshold, int friendlyThreshold) {
        this.id = id;
        this.displayName = displayName;
        this.startingReputation = startingReputation;
        this.hostileThreshold = hostileThreshold;
        this.friendlyThreshold = friendlyThreshold;
    }

    @Override
    public String getSerializedName() { return id; }
    public Component getDisplayName() { return Component.literal(displayName); }
    public int getStartingReputation() { return startingReputation; }
    public int getHostileThreshold() { return hostileThreshold; }
    public int getFriendlyThreshold() { return friendlyThreshold; }
}