package net.netherway.starwarschaincode.race;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum Race implements StringRepresentable{
    HUMAN("human", "Humano", "Versátil, sem penalidades.", 0, false, 0, 0),
    WOOKIEE("wookiee", "Wookiee", "Forte e resistente, mas lento em espaços apertados.", 15, false, .175, -.015),
    TWILEK("twilek", "Twi'lek", "Bônus de carisma, vida um pouco menor.", 10, false, .035, -.01),
    KEL_DOR("keldor", "Kel Dor", "Raça acolhedora e paciente", 0, false, .025, 0),
    NAUTOLAN("nautolan", "Nautolano", "pexe", 0, true, 0, 0),
    ZABRAK("zabrak", "Zabrak", "zabrak", 20, false, -.105, 0);

    public static final Codec<Race> CODEC = StringRepresentable.fromEnum(Race::values);

    private final String id, name, description;
    private final double extraHealth;
    private final boolean waterBreathing;
    private final double extraSize;
    private final double extraSpeed;

    Race(String id, String name, String description, double extraHealth, boolean waterBreathing, double extraSize, double extraSpeed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.extraHealth = extraHealth;
        this.waterBreathing = waterBreathing;
        this.extraSize = extraSize;
        this.extraSpeed = extraSpeed;
    }

    @Override
    public String getSerializedName() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getExtraHealth() { return extraHealth; }
    public boolean hasWaterBreathing() { return waterBreathing; }
    public double getExtraSize() { return extraSize; }
    public double getExtraSpeed() { return extraSpeed; }
}
