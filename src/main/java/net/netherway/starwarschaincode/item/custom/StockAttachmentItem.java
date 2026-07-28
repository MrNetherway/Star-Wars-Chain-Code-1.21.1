package net.netherway.starwarschaincode.item.custom;

import net.minecraft.world.item.Item;
import net.netherway.starwarschaincode.item.AttachmentItem;

public class StockAttachmentItem extends Item implements AttachmentItem {
    private final String id;
    private final float damageMultiplier;

    public StockAttachmentItem(Properties properties, String id) {
        this(properties, id, 1.0f);
    }

    public StockAttachmentItem(Properties properties, String id, float damageMultiplier) {
        super(properties);
        this.id = id;
        this.damageMultiplier = damageMultiplier;
    }

    public String getId() {
        return id;
    }

    /**
     * Multiplicador aplicado em cima do damage base da arma enquanto esse stock
     * estiver instalado. 1.1f = +10% de dano.
     */
    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.STOCK;
    }
}