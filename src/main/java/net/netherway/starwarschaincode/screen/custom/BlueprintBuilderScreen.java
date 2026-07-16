package net.netherway.starwarschaincode.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.netherway.starwarschaincode.StarWarsChainCode;

public class BlueprintBuilderScreen extends AbstractContainerScreen<BlueprintBuilderMenu> {

    // TODO: trocar pela textura real em assets/starwarschaincode/textures/gui/blueprint_builder.json.png
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "textures/gui/blueprint_builder/blueprint_builder_gui.png");

    public BlueprintBuilderScreen(BlueprintBuilderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = this.imageWidth - 57;

        this.titleLabelX = this.imageWidth - 53;
        this.titleLabelY = this.imageHeight - 160;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}