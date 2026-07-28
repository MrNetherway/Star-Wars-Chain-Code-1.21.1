package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record ReloadWeaponPayload(boolean main, boolean off) implements CustomPacketPayload {

    public static final Type<ReloadWeaponPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    StarWarsChainCode.MOD_ID,
                    "reload_weapon"));

    public static final StreamCodec<ByteBuf, ReloadWeaponPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ReloadWeaponPayload::main,
                    ByteBufCodecs.BOOL, ReloadWeaponPayload::off,
                    ReloadWeaponPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}