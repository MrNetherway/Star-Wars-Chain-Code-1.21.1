package net.netherway.starwarschaincode.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.network.TravelToPlanetPayload;
import net.netherway.starwarschaincode.planet.PlanetData;
import net.netherway.starwarschaincode.planet.ModPlanets;
import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.planet.PlanetTravelUtil;

import java.util.ArrayList;
import java.util.List;

public class PlanetMapScreen extends Screen {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "textures/gui/planets/star_map_background.png");

    private static final int ICON_SIZE = 16;

    private double panX = 0, panY = 0;
    private double zoom = 1.0;
    private static final double MIN_ZOOM = 0.5, MAX_ZOOM = 3.0;

    private boolean dragging = false;
    private double lastMouseX, lastMouseY;

    private PlanetData hoveredPlanet = null;
    private float rotationTicks = 0f;

    public PlanetMapScreen() {
        super(Component.literal("Mapa Estelar"));
    }

    private static final int BACKGROUND_FILE_SIZE = 512;
    private static final int ICON_FILE_SIZE = 64;

    private int previousBlur;

    @Override
    protected void init() {
        super.init();
        previousBlur = this.minecraft.options.getMenuBackgroundBlurriness();
        this.minecraft.options.menuBackgroundBlurriness().set(0);
    }

    @Override
    public void onClose() {
        this.minecraft.options.menuBackgroundBlurriness().set(previousBlur);
        super.onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xFF000000);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int bgDrawSize = (int) (512 * zoom);

        applyNearestFilter(BACKGROUND);
        graphics.blit(BACKGROUND,
                centerX - bgDrawSize / 2 + (int) panX,
                centerY - bgDrawSize / 2 + (int) panY,
                bgDrawSize, bgDrawSize,
                0, 0,
                BACKGROUND_FILE_SIZE, BACKGROUND_FILE_SIZE,
                BACKGROUND_FILE_SIZE, BACKGROUND_FILE_SIZE);

        int iconDrawSize = (int) (ICON_SIZE * zoom);
        hoveredPlanet = getPlanetAt(mouseX, mouseY);

        for (PlanetData planet : ModPlanets.all().values()) {
            int[] pos = getIconPos(planet, centerX, centerY, iconDrawSize);

            if (planet == hoveredPlanet) {
                render3DPlanet(graphics, planet, pos[0] + iconDrawSize / 2, pos[1] + iconDrawSize / 2, iconDrawSize, partialTick);
            } else {
                applyNearestFilter(planet.icon());
                graphics.blit(planet.icon(), pos[0], pos[1],
                        iconDrawSize, iconDrawSize,
                        0, 0,
                        ICON_FILE_SIZE, ICON_FILE_SIZE,
                        ICON_FILE_SIZE, ICON_FILE_SIZE);
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        if (hoveredPlanet != null) {
            renderPlanetTooltip(graphics, hoveredPlanet, mouseX, mouseY);
        }

        rotationTicks += partialTick;
    }

    private void render3DPlanet(GuiGraphics graphics, PlanetData planet, int centerX, int centerY, int size, float partialTick) {
        var poseStack = graphics.pose();
        poseStack.pushPose();

        // move pro centro do ícone
        poseStack.translate(centerX, centerY, 100);

        // escala proporcional ao tamanho do ícone (ajusta o multiplicador conforme necessário)
        float scale = size * 1.2f;
        poseStack.scale(scale, -scale, scale); // Y invertido pq GUI é Y-down

        // rotação diagonal contínua
        float angle = (rotationTicks + partialTick) * 1.5f;
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(angle));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(20f)); // inclinação diagonal fixa

        var itemRenderer = this.minecraft.getItemRenderer();
        itemRenderer.renderStatic(
                planet.modelItem(),
                net.minecraft.world.item.ItemDisplayContext.GUI,
                15728880, // lightmap full bright
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                poseStack,
                graphics.bufferSource(),
                this.minecraft.level,
                0
        );

        poseStack.popPose();
    }

    private void renderPlanetTooltip(GuiGraphics graphics, PlanetData planet, int mouseX, int mouseY) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(planet.displayName()).withStyle(ChatFormatting.YELLOW));

        PlanetData current = getCurrentPlanet();
        if (current != null && !current.id().equals(planet.id())) {
            double dist = PlanetTravelUtil.distanceBetween(current, planet);
            int cost = PlanetTravelUtil.calculateTibannaCost(dist);
            lines.add(Component.translatable("gui.starwarschaincode.planet_map.distance", String.format("%.1f", dist))
                    .withStyle(ChatFormatting.GRAY));
            lines.add(Component.translatable("gui.starwarschaincode.planet_map.cost", cost)
                    .withStyle(ChatFormatting.AQUA));
        } else if (current != null) {
            lines.add(Component.translatable("gui.starwarschaincode.planet_map.current_location")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        graphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
    }

    private PlanetData getCurrentPlanet() {
        var dim = this.minecraft.player.level().dimension();
        return ModPlanets.all().values().stream()
                .filter(p -> p.dimension().equals(dim))
                .findFirst().orElse(null);
    }

    private void applyNearestFilter(ResourceLocation texture) {
        var tex = this.minecraft.getTextureManager().getTexture(texture);
        RenderSystem.setShaderTexture(0, texture);
        tex.setFilter(false, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            PlanetData clicked = getPlanetAt(mouseX, mouseY);
            if (clicked != null) {
                PacketDistributor.sendToServer(new TravelToPlanetPayload(clicked.id()));
                this.onClose();
                return true;
            }
            dragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && button == 0) {
            panX += mouseX - lastMouseX;
            panY += mouseY - lastMouseY;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom + scrollY * 0.1));
        return true;
    }

    private int[] getIconPos(PlanetData planet, int centerX, int centerY, int iconSize) {
        int px = centerX + (int) ((planet.mapX() - 256) * zoom) + (int) panX - iconSize / 2;
        int py = centerY + (int) ((planet.mapY() - 256) * zoom) + (int) panY - iconSize / 2;
        return new int[]{px, py};
    }

    private PlanetData getPlanetAt(double mouseX, double mouseY) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int iconSize = (int) (ICON_SIZE * zoom);

        for (PlanetData planet : ModPlanets.all().values()) {
            int[] pos = getIconPos(planet, centerX, centerY, iconSize);
            if (mouseX >= pos[0] && mouseX <= pos[0] + iconSize && mouseY >= pos[1] && mouseY <= pos[1] + iconSize) {
                return planet;
            }
        }
        return null;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}