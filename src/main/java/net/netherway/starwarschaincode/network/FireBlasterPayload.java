package net.netherway.starwarschaincode.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record FireBlasterPayload() implements CustomPacketPayload {

    public static final Type<FireBlasterPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "fire_blaster"));

    public static final StreamCodec<FriendlyByteBuf, FireBlasterPayload> STREAM_CODEC =
            StreamCodec.unit(new FireBlasterPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}