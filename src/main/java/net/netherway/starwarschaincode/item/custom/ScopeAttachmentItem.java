package net.netherway.starwarschaincode.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.item.AttachmentItem;

public class ScopeAttachmentItem extends Item implements AttachmentItem {
    private final String id;
    private final float zoomDivisor;
    private final float sensitivityMultiplier;
    private final float zoomSpeed;
    private final ResourceLocation overlayTexture;

    public ScopeAttachmentItem(Properties properties, String id) {
        this(properties, id, 4.0f, 0.5f, 0.2f);
    }

    public ScopeAttachmentItem(Properties properties, String id, float zoomDivisor,
                               float sensitivityMultiplier, float zoomSpeed) {
        super(properties);
        this.id = id;
        this.zoomDivisor = zoomDivisor;
        this.sensitivityMultiplier = sensitivityMultiplier;
        this.zoomSpeed = zoomSpeed;
        this.overlayTexture = ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID,
                "textures/gui/scope/" + id + ".png");
    }

    public String getId() {
        return id;
    }

    /**
     * FOV atual é dividido por esse valor quando o scope está com zoom total (progress = 1).
     * Quanto maior, mais zoom.
     */
    public float getZoomDivisor() {
        return zoomDivisor;
    }

    /**
     * Multiplicador aplicado em cima da sensibilidade atual do player enquanto scoped.
     * 0.5f = metade da sensibilidade normal.
     */
    public float getSensitivityMultiplier() {
        return sensitivityMultiplier;
    }

    /**
     * Fração da distância restante percorrida por tick na transição de zoom (entrando ou saindo).
     * Maior = transição mais rápida/brusca. Menor = mais lenta/suave.
     */
    public float getZoomSpeed() {
        return zoomSpeed;
    }

    public ResourceLocation getOverlayTexture() {
        return overlayTexture;
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.SCOPE;
    }
}