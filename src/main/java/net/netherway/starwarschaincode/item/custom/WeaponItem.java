package net.netherway.starwarschaincode.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.netherway.starwarschaincode.client.ModKeyMappings;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.item.client.WeaponItemRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class WeaponItem extends Item implements GeoItem {

    private final float damage;
    private final float projectileSpeed;
    private final int fireRate;
    private final float fireDistance;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WeaponItem(Properties properties, float damage, float projectileSpeed, int fireRate, float fireDistance) {
        super(properties);
        this.damage = damage;
        this.projectileSpeed = projectileSpeed;
        this.fireRate = fireRate;
        this.fireDistance = fireDistance;
    }

    public float getDamage() { return damage; }
    public float getProjectileSpeed() { return projectileSpeed; }
    public int getFireRate() { return fireRate; }
    public float getFireDistance() { return fireDistance; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (slotChanged) {
            return true; // trocou de slot de verdade (ex: pegou outro item), aí sim reequipa
        }
        return oldStack.getItem() != newStack.getItem(); // só reequipa se o TIPO do item mudou, ignora mudança de componentes
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "weapon_controller", 2, state -> {
            net.minecraft.client.player.LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
            boolean isFirstPersonView = net.minecraft.client.Minecraft.getInstance().options.getCameraType().isFirstPerson();

            if (player != null && player.isSprinting() && isFirstPersonView) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
            }

            return PlayState.STOP;
        })
                .triggerableAnim("shoot", RawAnimation.begin().thenPlay("shoot"))
                .triggerableAnim("reload_eject", RawAnimation.begin().thenPlay("reload_eject"))
                .triggerableAnim("reload_insert", RawAnimation.begin().thenPlay("reload_insert")));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(stack.get(ModDataComponents.TIBANNA_AMOUNT) == null) {
            tooltipComponents.add(Component.translatable("data.starwarschaincode.null_tibanna").withStyle(ChatFormatting.DARK_RED));
            return;
        }

        tooltipComponents.add(Component.translatable("data.starwarschaincode.weapon_tibanna",
                stack.get(ModDataComponents.TIBANNA_AMOUNT)).withStyle(ChatFormatting.DARK_GREEN));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}