package net.netherway.starwarschaincode.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;

public record ShipRotationPayload(float deltaYaw, float deltaPitch, byte flags) implements CustomPacketPayload {

    public static final Type<ShipRotationPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "ship_rotation"));

    public static final StreamCodec<ByteBuf, ShipRotationPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ShipRotationPayload::deltaYaw,
            ByteBufCodecs.FLOAT, ShipRotationPayload::deltaPitch,
            ByteBufCodecs.BYTE, ShipRotationPayload::flags,
            ShipRotationPayload::new
    );

    // Bits do flag byte
    private static final int ROLL_LEFT = 1;
    private static final int ROLL_RIGHT = 1 << 1;
    private static final int THRUST_FORWARD = 1 << 2;
    private static final int THRUST_BACKWARD = 1 << 3;
    private static final int ASCEND = 1 << 4;
    private static final int DESCEND = 1 << 5;

    public static byte packFlags(boolean rollLeft, boolean rollRight, boolean thrustForward,
                                 boolean thrustBackward, boolean ascend, boolean descend) {
        byte flags = 0;
        if (rollLeft) flags |= ROLL_LEFT;
        if (rollRight) flags |= ROLL_RIGHT;
        if (thrustForward) flags |= THRUST_FORWARD;
        if (thrustBackward) flags |= THRUST_BACKWARD;
        if (ascend) flags |= ASCEND;
        if (descend) flags |= DESCEND;
        return flags;
    }

    public boolean rollLeft() { return (flags & ROLL_LEFT) != 0; }
    public boolean rollRight() { return (flags & ROLL_RIGHT) != 0; }
    public boolean thrustForward() { return (flags & THRUST_FORWARD) != 0; }
    public boolean thrustBackward() { return (flags & THRUST_BACKWARD) != 0; }
    public boolean ascend() { return (flags & ASCEND) != 0; }
    public boolean descend() { return (flags & DESCEND) != 0; }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}