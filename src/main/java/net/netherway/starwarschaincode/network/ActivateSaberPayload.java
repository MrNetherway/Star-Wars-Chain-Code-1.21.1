package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record ActivateSaberPayload(InteractionHand hand) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<ActivateSaberPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "activate_saber"));

    public static final StreamCodec<ByteBuf, ActivateSaberPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    payload -> payload.hand() == InteractionHand.OFF_HAND,
                    isOffhand -> new ActivateSaberPayload(isOffhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
