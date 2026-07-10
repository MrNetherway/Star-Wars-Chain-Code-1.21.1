package net.netherway.starwarschaincode.race;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.netherway.starwarschaincode.sound.ModSounds;
import net.netherway.starwarschaincode.util.DelayedTaskScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaceAbilities {

    private static final Map<UUID, Long> lastUsed = new HashMap<>();
    private static final long COOLDOWN_TICKS = 15 * 20;

    public static void activate(ServerPlayer player, int slot) {
        Race race = player.getData(RaceAttachments.PLAYER_RACE);
        long now = player.level().getGameTime();
        long last = lastUsed.getOrDefault(player.getUUID(), 0L);

        if (now - last < COOLDOWN_TICKS) {
            long remaining = (COOLDOWN_TICKS - (now - last)) / 20;
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Recarregando... " + remaining + "s"), true);
            return;
        }

        lastUsed.put(player.getUUID(), now);

        switch (race) {
            case WOOKIEE -> {
                if (slot == 1) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_RESISTANCE, 10*20, 1, true, true

                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_BOOST, 10*20, 1, true, true

                    ));

                    player.level().playSound(
                            null,
                            player.blockPosition(),
                            ModSounds.WOOKIEE_ABILITY_1.get(),
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F
                    );

                    DelayedTaskScheduler.schedule(player.serverLevel(), 10*20, () -> {
                        player.addEffect(new MobEffectInstance(
                                MobEffects.MOVEMENT_SLOWDOWN, 3*20, 1, true, false, false

                        ));
                    });
                } else if (slot == 2) {

                }
            }
            case KEL_DOR -> {

            }
            case ZABRAK -> {
                if (slot == 1) {
                    if (player.getHealth() < player.getHealth()+race.getExtraHealth() && player.getFoodData().getFoodLevel() >= 4){
                        player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()-4);
                        player.setHealth(player.getHealth()+4);
                    }
                } else if (slot == 2) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_BOOST, 15*20, 1, true, true

                    ));

                    DelayedTaskScheduler.schedule(player.serverLevel(), 15*20, () -> {
                        player.addEffect(new MobEffectInstance(
                                MobEffects.WEAKNESS, 5*20, 1, true, false, false

                        ));
                    });
                }
            }
            default -> {}
        }
    }
}
