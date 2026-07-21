package net.netherway.starwarschaincode.attachment;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class HyperspaceTravelData {

    public static final int ZOOM_IN_TICKS = 20;
    public static final int ZOOM_OUT_TICKS = 2;
    public static final int RECOVERY_TICKS = 20; // FOV volta ao normal depois do overlay aparecer

    public static final HyperspaceTravelData IDLE =
            new HyperspaceTravelData(false, null, 0, 0, 0, 0, 0, 0, -1);

    public final boolean traveling;
    public final ResourceKey<Level> targetDimension;
    public final double destX, destY, destZ;
    public final float destYaw;
    public final long startGameTime;
    public final int durationTicks;
    public final int shipEntityId;

    public HyperspaceTravelData(boolean traveling, ResourceKey<Level> targetDimension,
                                double destX, double destY, double destZ, float destYaw,
                                long startGameTime, int durationTicks, int shipEntityId) {
        this.traveling = traveling;
        this.targetDimension = targetDimension;
        this.destX = destX;
        this.destY = destY;
        this.destZ = destZ;
        this.destYaw = destYaw;
        this.startGameTime = startGameTime;
        this.durationTicks = durationTicks;
        this.shipEntityId = shipEntityId;
    }

    // Codec manual (não StreamCodec.composite, que só vai até 6 campos)
    public static final StreamCodec<RegistryFriendlyByteBuf, HyperspaceTravelData> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public HyperspaceTravelData decode(RegistryFriendlyByteBuf buf) {
                    boolean traveling = buf.readBoolean();
                    ResourceKey<Level> dim = buf.readBoolean()
                            ? ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation())
                            : null;
                    double x = buf.readDouble();
                    double y = buf.readDouble();
                    double z = buf.readDouble();
                    float yaw = buf.readFloat();
                    long start = buf.readVarLong();
                    int duration = buf.readVarInt();
                    int shipId = buf.readVarInt();
                    return new HyperspaceTravelData(traveling, dim, x, y, z, yaw, start, duration, shipId);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, HyperspaceTravelData data) {
                    buf.writeBoolean(data.traveling);
                    buf.writeBoolean(data.targetDimension != null);
                    if (data.targetDimension != null) {
                        buf.writeResourceLocation(data.targetDimension.location());
                    }
                    buf.writeDouble(data.destX);
                    buf.writeDouble(data.destY);
                    buf.writeDouble(data.destZ);
                    buf.writeFloat(data.destYaw);
                    buf.writeVarLong(data.startGameTime);
                    buf.writeVarInt(data.durationTicks);
                    buf.writeVarInt(data.shipEntityId);
                }
            };
}