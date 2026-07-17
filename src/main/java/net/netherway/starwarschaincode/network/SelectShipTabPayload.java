package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record SelectShipTabPayload(int shipIndex) implements CustomPacketPayload {

    public static final Type<SelectShipTabPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "select_ship_tab"));

    public static final StreamCodec<ByteBuf, SelectShipTabPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, SelectShipTabPayload::shipIndex, SelectShipTabPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}