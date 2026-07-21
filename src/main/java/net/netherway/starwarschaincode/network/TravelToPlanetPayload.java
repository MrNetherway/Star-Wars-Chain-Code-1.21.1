package net.netherway.starwarschaincode.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record TravelToPlanetPayload(ResourceLocation planetId) implements CustomPacketPayload {

    public static final Type<TravelToPlanetPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "travel_to_planet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TravelToPlanetPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, TravelToPlanetPayload::planetId,
                    TravelToPlanetPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}