package net.netherway.starwarschaincode.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.netherway.starwarschaincode.component.ModDataComponents;

import java.util.List;

public class PortableSolarCollectorItem extends Item {
    private int maxSolar = 100;
    private boolean sunVisible = false;

    public PortableSolarCollectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if(level.isClientSide())
            return;

        BlockPos pos = entity.blockPosition();

        if (level.isDay() && level.canSeeSky(pos)) {
            sunVisible = true;
            if (level.getGameTime() % 20 == 0) {
                Integer solar = stack.getOrDefault(ModDataComponents.SOLAR_AMOUNT, 0);

                if (solar < maxSolar) {
                    stack.set(ModDataComponents.SOLAR_AMOUNT, solar + 1);
                }
            }
        } else {
            sunVisible = false;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(stack.get(ModDataComponents.SOLAR_AMOUNT) != null) {
            tooltipComponents.add(Component.literal("Total Solar Energy Captured: " + stack.get(ModDataComponents.SOLAR_AMOUNT) + "/" + maxSolar));
        }

        tooltipComponents.add(Component.literal(sunVisible ? "The sun is visible!" : "The sun is not visible!")
                .withStyle(sunVisible ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
