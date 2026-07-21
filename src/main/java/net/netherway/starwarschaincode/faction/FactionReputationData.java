package net.netherway.starwarschaincode.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;

import java.util.EnumMap;
import java.util.Map;

public record FactionReputationData(Map<Faction, Integer> reputation) {

    public static final Codec<Map<Faction, Integer>> MAP_CODEC =
            Codec.unboundedMap(Faction.CODEC, Codec.INT);

    public static final Codec<FactionReputationData> CODEC =
            MAP_CODEC.xmap(FactionReputationData::new, FactionReputationData::reputation);

    public static FactionReputationData empty() {
        return new FactionReputationData(new EnumMap<>(Faction.class));
    }

    public int get(Faction faction) {
        return reputation.getOrDefault(faction, faction.getStartingReputation());
    }

    public FactionReputationData with(Faction faction, int value) {
        Map<Faction, Integer> copy = new EnumMap<>(Faction.class);
        copy.putAll(reputation);
        copy.put(faction, value);
        return new FactionReputationData(copy);
    }
}