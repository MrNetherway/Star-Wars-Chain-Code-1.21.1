package net.netherway.starwarschaincode.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import net.netherway.starwarschaincode.network.ReloadCompletePayload;
import software.bernie.geckolib.animatable.GeoItem;

public class ReloadAnimHandler {

    private static boolean isFirstPersonView(Minecraft mc) {
        return mc.options.getCameraType().isFirstPerson();
    }

    public static void onReloadComplete(ReloadCompletePayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (payload.main() && payload.mainInserted()) {
            ItemStack stack = mc.player.getMainHandItem();
            if (stack.getItem() instanceof WeaponItem w) {
                if(!isFirstPersonView(mc)) return;
                w.triggerAnim(mc.player, GeoItem.getId(stack), "weapon_controller", "reload_insert");
            }
        }

        if (payload.off() && payload.offInserted()) {
            ItemStack stack = mc.player.getOffhandItem();
            if (stack.getItem() instanceof WeaponItem w) {
                if(!isFirstPersonView(mc)) return;
                w.triggerAnim(mc.player, GeoItem.getId(stack), "weapon_controller", "reload_insert");
            }
        }
    }
}