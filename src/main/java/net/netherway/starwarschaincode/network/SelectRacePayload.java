package net.netherway.starwarschaincode.network;

import net.netherway.starwarschaincode.race.Race;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SelectRacePayload(Race race) implements CustomPacketPayload {

    public static final Type<SelectRacePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("starwarschaincode", "select_race"));

    private static final StreamCodec<ByteBuf, Race> RACE_CODEC =
            ByteBufCodecs.STRING_UTF8.map(Race::valueOf, Enum::name);

    public static final StreamCodec<ByteBuf, SelectRacePayload> STREAM_CODEC =
            RACE_CODEC.map(SelectRacePayload::new, SelectRacePayload::race);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}