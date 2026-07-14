package net.netherway.starwarschaincode.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.netherway.starwarschaincode.StarWarsChainCode;

public class ChargedChamberScreen extends AbstractContainerScreen<ChargedChamberMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "textures/gui/charged_chamber/charged_chamber_gui.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "textures/gui/arrow_progress.png");
    private static final ResourceLocation BAR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "textures/gui/energy_progress.png");

    public ChargedChamberScreen(AbstractContainerMenu menu, Inventory playerInventory, Component title) {
        super((ChargedChamberMenu) menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.setShaderTexture(0,GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0,0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
        renderProgressBar(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(ARROW_TEXTURE,x + 76, y + 26, 0, 0, menu.getScaledArrowProgress(), 16, 24, 16);
        }
    }

    private void renderProgressBar(GuiGraphics guiGraphics, int x, int y) {
        if(menu.addedLava()) {
            guiGraphics.blit(BAR_TEXTURE,x + 8, y + 26 + (41 - menu.getScaledBarProgress()), 0, 41 - menu.getScaledBarProgress(),
                    3, menu.getScaledBarProgress(), 3, 41);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
