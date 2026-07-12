package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record LightsaberImpulsePayload(int chargeTicks) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LightsaberImpulsePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "lightsaber_impulse"));

    public static final StreamCodec<ByteBuf, LightsaberImpulsePayload> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(LightsaberImpulsePayload::new, LightsaberImpulsePayload::chargeTicks);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
