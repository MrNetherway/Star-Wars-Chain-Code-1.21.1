package net.netherway.starwarschaincode.item.custom;

import net.minecraft.world.item.Item;
import net.netherway.starwarschaincode.item.AttachmentItem;

public class BarrelAttachmentItem extends Item implements AttachmentItem {
    private final String id;
    private final float fireDistanceMultiplier;

    public BarrelAttachmentItem(Properties properties, String id) {
        this(properties, id, 1.0f);
    }

    public BarrelAttachmentItem(Properties properties, String id, float fireDistanceMultiplier) {
        super(properties);
        this.id = id;
        this.fireDistanceMultiplier = fireDistanceMultiplier;
    }

    public String getId() {
        return id;
    }

    /**
     * Multiplicador aplicado em cima da fireDistance base da arma enquanto esse cano
     * estiver instalado. 1.2f = +20% de alcance.
     */
    public float getFireDistanceMultiplier() {
        return fireDistanceMultiplier;
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.BARREL;
    }
}