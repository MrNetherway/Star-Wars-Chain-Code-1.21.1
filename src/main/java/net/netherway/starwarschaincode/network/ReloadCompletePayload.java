package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record ReloadCompletePayload(boolean main, boolean off, boolean mainInserted, boolean offInserted)
        implements CustomPacketPayload {

    public static final Type<ReloadCompletePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "reload_complete"));

    public static final StreamCodec<ByteBuf, ReloadCompletePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ReloadCompletePayload::main,
                    ByteBufCodecs.BOOL, ReloadCompletePayload::off,
                    ByteBufCodecs.BOOL, ReloadCompletePayload::mainInserted,
                    ByteBufCodecs.BOOL, ReloadCompletePayload::offInserted,
                    ReloadCompletePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}