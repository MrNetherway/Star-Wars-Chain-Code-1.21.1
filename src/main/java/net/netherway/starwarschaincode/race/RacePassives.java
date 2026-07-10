package net.netherway.starwarschaincode.race;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = "starwarschaincode")
public class RacePassives {
    private static boolean hasWeakness = false;

    private static final ResourceLocation HEALTH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("starwarschaincode", "race_health_bonus");
    private static final ResourceLocation SIZE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("starwarschaincode", "race_size_modifier");
    private static final ResourceLocation SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("starwarschaincode", "race_speed_modifier");

    private static final float RANGED_BONUS_MULTIPLIER = 2.5f;

    private static final int FOOD_THRESHOLD = 10;
    private static final int CHECK_INTERVAL_TICKS = 20;

    public static void apply(ServerPlayer player, Race race) {
        // --- Corações extra ---
        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.removeModifier(HEALTH_MODIFIER_ID);
            if (race.getExtraHealth() != 0) {
                healthAttr.addOrReplacePermanentModifier(new AttributeModifier(
                        HEALTH_MODIFIER_ID, race.getExtraHealth(), AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

        // --- Respiração debaixo d'água ---
        if (race.hasWaterBreathing()) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.WATER_BREATHING, MobEffectInstance.INFINITE_DURATION, 0, false, false, false
            ));
        } else {
            player.removeEffect(MobEffects.WATER_BREATHING);
        }

        AttributeInstance sizeAttr = player.getAttribute(Attributes.SCALE);
        if(sizeAttr != null) {
            sizeAttr.removeModifier(SIZE_MODIFIER_ID);
            if(race.getExtraSize() != 0) {
                sizeAttr.addOrReplacePermanentModifier(new AttributeModifier(
                        SIZE_MODIFIER_ID, race.getExtraSize(), AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if(speedAttr != null) {
            speedAttr.removeModifier(SPEED_MODIFIER_ID);
            if(race.getExtraSpeed() != 0) {
                speedAttr.addOrReplacePermanentModifier(new AttributeModifier(
                        SPEED_MODIFIER_ID, race.getExtraSpeed(), AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

    }

    @SubscribeEvent
    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow
                && arrow.getOwner() instanceof ServerPlayer shooter) {

            Race race = shooter.getData(RaceAttachments.PLAYER_RACE);

            if (race == Race.WOOKIEE) {
                arrow.setBaseDamage(arrow.getBaseDamage() * RANGED_BONUS_MULTIPLIER);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        long gameTime = event.getServer().overworld().getGameTime();
        if (gameTime % CHECK_INTERVAL_TICKS != 0) return; // só roda 1 vez por segundo

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            boolean lowFood = player.getFoodData().getFoodLevel() < FOOD_THRESHOLD;
            Race race = player.getData(RaceAttachments.PLAYER_RACE);

            if (lowFood && race == Race.WOOKIEE && !hasWeakness) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, MobEffectInstance.INFINITE_DURATION, 1, true, false, false));
                hasWeakness = true;
            } else if (!lowFood && race == Race.WOOKIEE && hasWeakness) {
                player.removeEffect(MobEffects.WEAKNESS);
                hasWeakness = false;
            }
        }
    }
}
