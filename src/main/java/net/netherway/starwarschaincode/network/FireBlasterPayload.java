package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record FireBlasterPayload(InteractionHand hand) implements CustomPacketPayload {

    public static final Type<FireBlasterPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "fire_blaster"));

    public static final StreamCodec<ByteBuf, FireBlasterPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    payload -> payload.hand() == InteractionHand.OFF_HAND,
                    isOffhand -> new FireBlasterPayload(isOffhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}