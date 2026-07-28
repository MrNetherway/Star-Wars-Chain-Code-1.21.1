package net.netherway.starwarschaincode.client;

import net.minecraft.client.Minecraft;
import net.netherway.starwarschaincode.item.custom.ScopeAttachmentItem;

/**
 * Guarda o estado de "scoped" do cliente. Só existe client-side.
 */
public class ScopeClientState {
    private static boolean scoped = false;
    private static ScopeAttachmentItem activeScope = null;
    private static double storedSensitivity = -1;

    // Progresso do zoom suave: 0 = sem zoom nenhum, 1 = zoom total do scope ativo.
    private static float zoomProgress = 0f;
    private static float prevZoomProgress = 0f;

    public static boolean isScoped() {
        return scoped;
    }

    public static ScopeAttachmentItem getActiveScope() {
        return activeScope;
    }

    public static void enable(ScopeAttachmentItem scope) {
        if (scoped || scope == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        storedSensitivity = mc.options.sensitivity().get();
        mc.options.sensitivity().set(storedSensitivity * scope.getSensitivityMultiplier());

        scoped = true;
        activeScope = scope;
    }

    public static void disable() {
        if (!scoped) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (storedSensitivity >= 0) {
            mc.options.sensitivity().set(storedSensitivity);
        }

        scoped = false;
        storedSensitivity = -1;
        // activeScope NÃO é limpo aqui de propósito: o FOV ainda precisa "desenrolar"
        // suavemente até 0 usando o divisor/velocidade desse scope. Ele só é solto em
        // tick(), quando o zoomProgress já chegou em 0.
    }

    /**
     * Chama uma vez por tick (client tick, não render tick) pra avançar a transição de zoom.
     */
    public static void tick() {
        prevZoomProgress = zoomProgress;

        float target = scoped ? 1f : 0f;

        if (zoomProgress != target) {
            // Usa a velocidade do scope atualmente ativo (mesmo desativando, ele continua
            // sendo o activeScope até o progresso zerar por completo).
            float speed = activeScope != null ? activeScope.getZoomSpeed() : 0.2f;

            zoomProgress += (target - zoomProgress) * speed;

            // Fecha o resto do caminho quando já tá bem perto, senão ele converge pra
            // sempre e nunca chega exatamente em 0 ou 1.
            if (Math.abs(target - zoomProgress) < 0.001f) {
                zoomProgress = target;
            }
        }

        if (!scoped && zoomProgress <= 0f) {
            activeScope = null;
        }
    }

    /**
     * Progresso do zoom interpolado entre o tick anterior e o atual, usando o partialTick
     * do frame — isso é o que deixa a transição suave mesmo em fps alto, e não travada
     * em incrementos de tick (20/s).
     */
    public static float getZoomProgress(float partialTick) {
        return prevZoomProgress + (zoomProgress - prevZoomProgress) * partialTick;
    }
}