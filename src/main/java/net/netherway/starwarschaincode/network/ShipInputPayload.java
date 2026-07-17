package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record ShipInputPayload(boolean left, boolean right, boolean up, boolean down) implements CustomPacketPayload {

    public static final Type<ShipInputPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "ship_input"));

    public static final StreamCodec<ByteBuf, ShipInputPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ShipInputPayload::left,
            ByteBufCodecs.BOOL, ShipInputPayload::right,
            ByteBufCodecs.BOOL, ShipInputPayload::up,
            ByteBufCodecs.BOOL, ShipInputPayload::down,
            ShipInputPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}