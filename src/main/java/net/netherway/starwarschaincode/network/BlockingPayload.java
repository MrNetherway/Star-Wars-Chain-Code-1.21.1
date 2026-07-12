package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record BlockingPayload(boolean isBlocking) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BlockingPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "blocking"));

    public static final StreamCodec<ByteBuf, BlockingPayload> STREAM_CODEC =
            ByteBufCodecs.BOOL.map(BlockingPayload::new, BlockingPayload::isBlocking);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
