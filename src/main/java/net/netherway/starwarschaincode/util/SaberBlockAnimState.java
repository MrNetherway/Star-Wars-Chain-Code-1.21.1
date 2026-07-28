package net.netherway.starwarschaincode.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.item.custom.SaberBlockAnimNames;
import net.netherway.starwarschaincode.network.PlayThirdPersonAnimPayload;
import net.netherway.starwarschaincode.network.StopThirdPersonAnimPayload;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SaberBlockAnimState {
    private static final Map<UUID, String> ACTIVE_ANIM = new ConcurrentHashMap<>();

    public static void startBlock(ServerPlayer player, ItemStack stack) {
        triggerRandomAnim(player, stack);
    }

    public static void onSuccessfulParry(ServerPlayer player, ItemStack stack) {
        triggerRandomAnim(player, stack);
    }

    private static void triggerRandomAnim(ServerPlayer player, ItemStack stack) {
        if (!(stack.getItem() instanceof LightsaberItem))
            return;

        String animName = SaberBlockAnimNames.NAMES[player.getRandom().nextInt(SaberBlockAnimNames.NAMES.length)];
        ACTIVE_ANIM.put(player.getUUID(), animName);

        // só PAL (terceira pessoa) — GeckoLib do sabre é decidido no client, ver ThirdPersonAnimHandler
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new PlayThirdPersonAnimPayload(player.getId(), animName, false));
    }

    public static void stopBlock(ServerPlayer player, ItemStack stack) {
        ACTIVE_ANIM.remove(player.getUUID());

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new StopThirdPersonAnimPayload(player.getId(), false));
    }
}