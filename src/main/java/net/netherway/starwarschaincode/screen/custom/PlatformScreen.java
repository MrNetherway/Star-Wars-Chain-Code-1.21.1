package net.netherway.starwarschaincode.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.network.SelectShipTabPayload;

import java.util.ArrayList;
import java.util.List;

public class PlatformScreen extends AbstractContainerScreen<PlatformMenu> {

    private static final ResourceLocation TEXTURE_MAIN = ResourceLocation.fromNamespaceAndPath(
            StarWarsChainCode.MOD_ID, "textures/gui/platform_controller/platform_menu_gui.png");
    private static final ResourceLocation TEXTURE_COMPONENTS = ResourceLocation.fromNamespaceAndPath(
            StarWarsChainCode.MOD_ID, "textures/gui/platform_controller/platform_ship_menu_gui.png");

    private int lastKnownShipCount = -1;
    private int tickCounter = 0;
    private static final int REFRESH_INTERVAL = 10; // a cada 10 ticks (~0.5s)

    public PlatformScreen(PlatformMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        rebuildTabButtons();
    }

    private final List<Button> tabButtons = new ArrayList<>();

    private void rebuildTabButtons() {
        tabButtons.forEach(this::removeWidget);
        tabButtons.clear();

        List<ShipEntity> ships = menu.getAvailableShips();
        lastKnownShipCount = ships.size();

        for (int i = 0; i < ships.size(); i++) {
            ShipEntity ship = ships.get(i);
            String shipName = ship.getShipType() != null
                    ? ship.getShipType().id().getPath()
                    : "ship";

            Button tabButton = Button.builder(
                    Component.literal(shipName.length() > 8 ? shipName.substring(0, 8) : shipName),
                    btn -> {
                        menu.selectShipById(ship.getUUID());
                        PacketDistributor.sendToServer(new SelectShipTabPayload(ship.getUUID()));
                    }
            ).bounds(leftPos + 8 + i * 42, topPos - 24, 40, 20).build();

            tabButtons.add(tabButton);
            this.addRenderableWidget(tabButton);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        tickCounter++;
        if (tickCounter >= REFRESH_INTERVAL) {
            tickCounter = 0;
            menu.refreshAvailableShips();

            if (menu.getAvailableShips().size() != lastKnownShipCount) {
                rebuildTabButtons();
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation texture = menu.getViewMode() == PlatformMenu.ViewMode.MAIN ? TEXTURE_MAIN : TEXTURE_COMPONENTS;
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}