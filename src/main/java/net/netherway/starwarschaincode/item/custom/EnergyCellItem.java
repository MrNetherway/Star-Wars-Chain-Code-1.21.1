package net.netherway.starwarschaincode.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.item.ModItems;

import java.util.List;

public class EnergyCellItem extends Item {
    private final int maxSolar = 1000;

    public EnergyCellItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide() || !(entity instanceof Player player)) {
            return;
        }

        if (ItemStack.matches(player.getOffhandItem(), stack)) {

            if (player.isCrouching() && player.getMainHandItem().is(ModItems.PORTABLE_SOLAR_COLLECTOR)) {
                ItemStack mainHandStack = player.getMainHandItem();

                if (level.getGameTime() % 10 != 0)
                    return;

                int panelSolar = mainHandStack.getOrDefault(ModDataComponents.ENERGY_AMOUNT, 0);
                int cellSolar = stack.getOrDefault(ModDataComponents.ENERGY_AMOUNT, 0);

                if (panelSolar > 0 && cellSolar < maxSolar) {
                    stack.set(ModDataComponents.ENERGY_AMOUNT, cellSolar + 1);
                    mainHandStack.set(ModDataComponents.ENERGY_AMOUNT, panelSolar - 1);

                    player.displayClientMessage(Component.translatable("data.starwarschaincode.transferring_energy",(cellSolar + 1), maxSolar)
                            .withStyle(ChatFormatting.GREEN), true);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.get(ModDataComponents.ENERGY_AMOUNT) != null) {
            tooltipComponents.add(Component.translatable("data.starwarschaincode.solar_captured",
                    stack.get(ModDataComponents.ENERGY_AMOUNT), maxSolar));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
