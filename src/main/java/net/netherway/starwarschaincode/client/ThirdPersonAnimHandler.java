package net.netherway.starwarschaincode.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.StarWarsChainCodeClient;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.network.PlayThirdPersonAnimPayload;
import net.netherway.starwarschaincode.network.StopThirdPersonAnimPayload;
import software.bernie.geckolib.animatable.GeoItem;

public class ThirdPersonAnimHandler {

    public static void play(PlayThirdPersonAnimPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Entity entity = mc.level.getEntity(payload.entityId());
        if (!(entity instanceof AbstractClientPlayer player)) return;

        ResourceLocation layerId = payload.offhand()
                ? StarWarsChainCodeClient.ANIMATION_LAYER_ID_LEFT
                : StarWarsChainCodeClient.ANIMATION_LAYER_ID_RIGHT;

        PlayerAnimationController controller = (PlayerAnimationController)
                PlayerAnimationAccess.getPlayerAnimationLayer(player, layerId);

        if (controller != null) {
            ResourceLocation animId = ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, payload.animName());
            controller.triggerAnimation(animId);
        }

        // GeckoLib do modelo do sabre: só o dono, só se estiver em 1ª pessoa
        if (player == mc.player && mc.options.getCameraType().isFirstPerson()) {
            ItemStack stack = payload.offhand() ? player.getOffhandItem() : player.getMainHandItem();
            if (stack.getItem() instanceof LightsaberItem saber) {
                long id = GeoItem.getId(stack);
                saber.triggerAnim(player, id, "block_controller", payload.animName());
            }
        }
    }

    public static void stop(StopThirdPersonAnimPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Entity entity = mc.level.getEntity(payload.entityId());
        if (!(entity instanceof AbstractClientPlayer player)) return;

        ResourceLocation layerId = payload.offhand()
                ? StarWarsChainCodeClient.ANIMATION_LAYER_ID_LEFT
                : StarWarsChainCodeClient.ANIMATION_LAYER_ID_RIGHT;

        PlayerAnimationController controller = (PlayerAnimationController)
                PlayerAnimationAccess.getPlayerAnimationLayer(player, layerId);

        if (controller != null) {
            controller.stopTriggeredAnimation();
        }

        if (player == mc.player && mc.options.getCameraType().isFirstPerson()) {
            ItemStack stack = payload.offhand() ? player.getOffhandItem() : player.getMainHandItem();
            if (stack.getItem() instanceof LightsaberItem saber) {
                long id = GeoItem.getId(stack);
                saber.stopTriggeredAnim(player, id, "block_controller", null);
            }
        }
    }
}