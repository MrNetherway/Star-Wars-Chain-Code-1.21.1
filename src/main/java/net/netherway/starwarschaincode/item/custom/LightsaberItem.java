package net.netherway.starwarschaincode.item.custom;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.network.PlayThirdPersonAnimPayload;
import net.netherway.starwarschaincode.util.SaberBlockAnimState;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;


@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class LightsaberItem extends Item implements GeoItem {

    private final double damage;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public LightsaberItem(Properties properties,
                          double damage) {
        super(properties);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);

        this.damage = damage;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player)) return;


        if (!level.isClientSide
                && stack.getOrDefault(ModDataComponents.ACTIVATED.get(), false)
                && player.getMainHandItem() != stack) {

            this.triggerAnim(player,
                    GeoItem.getId(stack),
                    "lightsaber_controller",
                    "lightsaber_deactivate");
            stack.set(ModDataComponents.ACTIVATED.get(), false);
        }
    }

    @SubscribeEvent
    public static void onItemDrop(ItemTossEvent event) {
        if (event.getPlayer().level().isClientSide()) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack stack = event.getEntity().getItem();

        if (stack.getItem() instanceof LightsaberItem lightsaber) {
            lightsaber.triggerAnim(
                    player,
                    GeoItem.getId(stack),
                    "lightsaber_controller",
                    "lightsaber_deactivate"
            );

            stack.set(ModDataComponents.ACTIVATED.get(), false);
        }
    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        for (ItemEntity item : event.getDrops()) {
            ItemStack stack = item.getItem();

            if (stack.getItem() instanceof LightsaberItem) {
                stack.set(ModDataComponents.ACTIVATED.get(), false);
            }
        }
    }

    public double getDamage() {
        return damage;
    }

    public static ItemAttributeModifiers createAttributes() {

        return ItemAttributeModifiers.builder()

                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(
                                        "starwarschaincode",
                                        "lightsaber_speed"
                                ),
                                -1.8,
                                Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )

                .build();
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof LightsaberItem))
            return;

        boolean activated = stack.getOrDefault(ModDataComponents.ACTIVATED.get(), false);
        boolean blocking = stack.getOrDefault(ModDataComponents.BLOCKING.get(), false);

        if (!activated || !blocking)
            return;

        if (!isFacingAttack(player, event.getSource()))
            return;

        event.setCanceled(true);
        SaberBlockAnimState.onSuccessfulParry(player, stack);
    }

    private static boolean isFacingAttack(ServerPlayer player, net.minecraft.world.damagesource.DamageSource source) {
        Vec3 sourcePos = source.getSourcePosition();

        if (sourcePos == null) {
            var direct = source.getDirectEntity();
            if (direct == null)
                return true; // sem posição pra checar (ex: fogo/queda), deixa bloquear
            sourcePos = direct.position();
        }

        Vec3 toSource = sourcePos.subtract(player.position()).normalize();
        Vec3 look = player.getLookAngle().normalize();

        double angleDeg = Math.toDegrees(Math.acos(Mth.clamp(look.dot(toSource), -1.0, 1.0)));

        return angleDeg <= 100.0; // cone de bloqueio — ajusta esse número (90-120 fica bom)
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        boolean activated = stack.getOrDefault(
                ModDataComponents.ACTIVATED.get(),
                false
        );

        boolean blocking = stack.getOrDefault(
                ModDataComponents.BLOCKING.get(),
                false
        );

        if (!activated || blocking)
            return false;


        target.hurt(
                attacker.damageSources().playerAttack((Player) attacker),
                (float) damage
        );

        return true;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "lightsaber_controller", 2,
                state -> PlayState.STOP)
                .triggerableAnim("lightsaber_activate", RawAnimation.begin().thenPlay("lightsaber_activate"))
                .triggerableAnim("lightsaber_deactivate", RawAnimation.begin().thenPlay("lightsaber_deactivate"))
                .triggerableAnim("saber_impulse", RawAnimation.begin().thenLoop("saber_impulse")));

        controllers.add(new AnimationController<>(this, "block_controller", 1,
                state -> PlayState.STOP)
                .triggerableAnim("saber_block_1", RawAnimation.begin().thenLoop("saber_block_1"))
                .triggerableAnim("saber_block_2", RawAnimation.begin().thenLoop("saber_block_2"))
                .triggerableAnim("saber_block_3", RawAnimation.begin().thenLoop("saber_block_3"))
                .triggerableAnim("saber_block_4", RawAnimation.begin().thenLoop("saber_block_4")));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (slotChanged) {
            return true; // trocou de slot de verdade (ex: pegou outro item), aí sim reequipa
        }
        return oldStack.getItem() != newStack.getItem(); // só reequipa se o TIPO do item mudou, ignora mudança de componentes
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
