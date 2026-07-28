package net.netherway.starwarschaincode.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.item.custom.ScopeAttachmentItem;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import net.netherway.starwarschaincode.util.WeaponAttachmentUtil;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class ScopeClientEvents {

    private static final int BLACK = 0xFF000000;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }

        while (ModKeyMappings.SCOPE_KEY.consumeClick()) {
            handleToggle(player);
        }

        if (ScopeClientState.isScoped()) {
            // Saiu da primeira pessoa -> desativa na hora.
            if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
                ScopeClientState.disable();
            } else {
                // Trocou de arma, tirou o scope, ou trocou de item na mão principal enquanto scoped.
                ItemStack main = player.getMainHandItem();
                ScopeAttachmentItem currentScope = (main.getItem() instanceof WeaponItem)
                        ? WeaponAttachmentUtil.getScope(main)
                        : null;

                if (currentScope == null || currentScope != ScopeClientState.getActiveScope()) {
                    ScopeClientState.disable();
                }
            }
        }

        // Avança a transição de zoom suave. Sempre chamado, mesmo desativado,
        // pra terminar de "desenrolar" o zoom até 0.
        ScopeClientState.tick();
    }

    private static void handleToggle(LocalPlayer player) {
        if (ScopeClientState.isScoped()) {
            ScopeClientState.disable();
            return;
        }

        // Não deixa mirar em terceira pessoa.
        if (Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }

        // Só a mão principal conta, offhand é ignorada mesmo que também tenha scope.
        ItemStack main = player.getMainHandItem();
        if (!(main.getItem() instanceof WeaponItem)) {
            return;
        }

        ScopeAttachmentItem scope = WeaponAttachmentUtil.getScope(main);
        if (scope == null) {
            return;
        }

        ScopeClientState.enable(scope);
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        ScopeAttachmentItem scope = ScopeClientState.getActiveScope();
        if (scope == null) {
            return;
        }

        float progress = ScopeClientState.getZoomProgress((float) event.getPartialTick());
        if (progress <= 0f) {
            return;
        }

        if (event.usedConfiguredFov()) {
            float baseFov = (float) event.getFOV();
            float zoomedFov = baseFov / scope.getZoomDivisor();
            event.setFOV(Mth.lerp(progress, baseFov, zoomedFov));
        }
    }

    // Cancela a layer da crosshair enquanto scoped.
    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (ScopeClientState.isScoped() && VanillaGuiLayers.CROSSHAIR.equals(event.getName())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiEvent.Post event) {
        ScopeAttachmentItem scope = ScopeClientState.getActiveScope();
        if (scope == null) {
            return;
        }

        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        float progress = ScopeClientState.getZoomProgress(partialTick);
        if (progress <= 0f) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int size = Math.min(width, height);

        int x = (width - size) / 2;
        int y = (height - size) / 2;
        int x1 = x + size;
        int y1 = y + size;

        RenderSystem.enableBlend();
        // Fade de alpha acompanhando o progresso do zoom, pra entrar/sair junto com o FOV.
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, progress);

        // textureWidth/textureHeight = size (não a resolução real do PNG) pra normalizar
        // o UV de 0 a 1 sempre, independente da resolução nativa da textura.
        graphics.blit(scope.getOverlayTexture(),
                x, y,
                0.0f, 0.0f,
                size, size,
                size, size);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Preenche o resto da tela (as sobras 16:9 que o quadrado não cobre) de preto sólido,
        // igual a luneta vanilla faz. Isso também recebe o fade, senão as barras aparecem
        // instantaneamente enquanto o círculo ainda tá surgindo.
        int barColor = ((int) (progress * 255) << 24) | 0x000000;
        graphics.fill(RenderType.guiOverlay(), 0, y1, width, height, barColor);
        graphics.fill(RenderType.guiOverlay(), 0, 0, width, y, barColor);
        graphics.fill(RenderType.guiOverlay(), 0, y, x, y1, barColor);
        graphics.fill(RenderType.guiOverlay(), x1, y, width, y1, barColor);

        RenderSystem.disableBlend();
    }
}