package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ActivateAbilityPayload(int slot) implements CustomPacketPayload {

    public static final Type<ActivateAbilityPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("starwarschaincode", "activate_ability"));

    public static final StreamCodec<ByteBuf, ActivateAbilityPayload> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(ActivateAbilityPayload::new, ActivateAbilityPayload::slot);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}