package net.netherway.starwarschaincode.item.custom;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
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
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.resources.ResourceLocation;


@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class LightsaberItem extends Item {

    private final double damage;

    public LightsaberItem(Properties properties,
                          double damage) {
        super(properties);


        this.damage = damage;
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

        if (!(event.getEntity() instanceof Player player))
            return;

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof LightsaberItem))
            return;

        boolean activated = stack.getOrDefault(
                ModDataComponents.ACTIVATED.get(),
                false
        );

        boolean blocking = stack.getOrDefault(
                ModDataComponents.BLOCKING.get(),
                false
        );

        if (!activated || !blocking)
            return;

        event.setCanceled(true);
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
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }
}
