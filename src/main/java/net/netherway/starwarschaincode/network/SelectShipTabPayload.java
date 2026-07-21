package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

import java.util.UUID;

public record SelectShipTabPayload(UUID shipId) implements CustomPacketPayload {

    public static final Type<SelectShipTabPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "select_ship_tab"));

    public static final StreamCodec<ByteBuf, SelectShipTabPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString),
                    SelectShipTabPayload::shipId,
                    SelectShipTabPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}