package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record StopThirdPersonAnimPayload(int entityId, boolean offhand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StopThirdPersonAnimPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID, "stop_third_person_anim"));

    public static final StreamCodec<ByteBuf, StopThirdPersonAnimPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, StopThirdPersonAnimPayload::entityId,
                    ByteBufCodecs.BOOL, StopThirdPersonAnimPayload::offhand,
                    StopThirdPersonAnimPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}