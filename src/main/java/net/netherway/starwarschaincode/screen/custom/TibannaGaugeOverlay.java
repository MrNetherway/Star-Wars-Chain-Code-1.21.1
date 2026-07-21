package net.netherway.starwarschaincode.screen.custom;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class TibannaGaugeOverlay {

    private static final ResourceLocation GAUGE_BG =
            ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "textures/gui/gauge_background.png");
    private static final ResourceLocation GAUGE_NEEDLE =
            ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "textures/gui/gauge_needle.png");

    // posição na tela e tamanho do medidor
    private static final int GAUGE_X = 10;
    private static final int GAUGE_Y = 10;
    private static final int GAUGE_SIZE = 64;

    // offset do pivot da agulha dentro da textura (geralmente o centro/base do semicírculo)
    private static final int PIVOT_X = GAUGE_SIZE / 2;
    private static final int PIVOT_Y = GAUGE_SIZE / 2;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        // só mostra se o player estiver pilotando a nave
        if (!(player.getVehicle() instanceof ShipEntity ship)) return;

        GuiGraphics graphics = event.getGuiGraphics();

        float gasAmount = ship.getTibannaGas();       // valor atual
        float gasMax = ship.getMaxTibannaGas();       // valor máximo
        float percent = Mth.clamp(gasAmount / gasMax, 0f, 1f);

        float angle = Mth.lerp(percent, -90f, 90f);

        // desenha o fundo
        graphics.blit(GAUGE_BG, GAUGE_X, GAUGE_Y, 0, 0, GAUGE_SIZE, GAUGE_SIZE, GAUGE_SIZE, GAUGE_SIZE);

        // rotaciona e desenha a agulha
        graphics.pose().pushPose();
        graphics.pose().translate(GAUGE_X + PIVOT_X, GAUGE_Y + PIVOT_Y, 0);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(angle));
        graphics.pose().translate(-PIVOT_X, -PIVOT_Y, 0);

        graphics.blit(GAUGE_NEEDLE, 0, 0, 0, 0, GAUGE_SIZE, GAUGE_SIZE, GAUGE_SIZE, GAUGE_SIZE);

        graphics.pose().popPose();
    }
}
