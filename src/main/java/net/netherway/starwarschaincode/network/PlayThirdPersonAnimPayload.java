package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record PlayThirdPersonAnimPayload(int entityId, String animName, boolean offhand) implements CustomPacketPayload {

    public static final Type<PlayThirdPersonAnimPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "play_thirdperson_anim"));

    public static final StreamCodec<ByteBuf, PlayThirdPersonAnimPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, PlayThirdPersonAnimPayload::entityId,
                    ByteBufCodecs.STRING_UTF8, PlayThirdPersonAnimPayload::animName,
                    ByteBufCodecs.BOOL, PlayThirdPersonAnimPayload::offhand,
                    PlayThirdPersonAnimPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}