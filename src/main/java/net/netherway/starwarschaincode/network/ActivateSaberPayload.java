package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record ActivateSaberPayload() implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<ActivateSaberPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "activate_saber"));

    public static final StreamCodec<ByteBuf, ActivateSaberPayload> STREAM_CODEC =
            StreamCodec.unit(new ActivateSaberPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
