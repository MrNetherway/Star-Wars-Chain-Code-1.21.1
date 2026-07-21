package net.netherway.starwarschaincode.planet;

import net.minecraft.util.Mth;
import net.netherway.starwarschaincode.attachment.HyperspaceTravelData;

public class PlanetTravelUtil {

    private static final int BASE_COST = 5;
    private static final double DISTANCE_MULTIPLIER = 0.2;
    private static final int BASE_TRAVEL_TICKS = 5;   // ~2s mínimo
    private static final double TICKS_PER_DISTANCE_UNIT = 1.6; // ajusta a escala pro seu sistema de distância

    public static int calculateTravelTicks(double distance) {
        int travelPhase = Mth.clamp(
                BASE_TRAVEL_TICKS + (int) Math.round(distance * TICKS_PER_DISTANCE_UNIT),
                BASE_TRAVEL_TICKS, 400
        );
        return HyperspaceTravelData.ZOOM_IN_TICKS
                + HyperspaceTravelData.ZOOM_OUT_TICKS
                + HyperspaceTravelData.RECOVERY_TICKS
                + travelPhase;
    }

    public static double distanceBetween(PlanetData a, PlanetData b) {
        double dx = a.mapX() - b.mapX();
        double dy = a.mapY() - b.mapY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static int calculateTibannaCost(double distance) {
        return BASE_COST + (int) Math.round(distance * DISTANCE_MULTIPLIER);
    }
}