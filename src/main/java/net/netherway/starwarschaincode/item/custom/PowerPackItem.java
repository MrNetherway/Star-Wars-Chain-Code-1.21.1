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

public class PowerPackItem extends Item {
    private final int maxTibanna = 30;

    public PowerPackItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide() || !(entity instanceof Player player)) {
            return;
        }

        if (ItemStack.matches(player.getOffhandItem(), stack)) {

            if (player.isCrouching() && player.getMainHandItem().is(ModItems.TIBANNA_GAS_CAPSULE)) {
                ItemStack mainHandStack = player.getMainHandItem();

                if (level.getGameTime() % 5 != 0)
                    return;

                int tibannaCapsule = mainHandStack.getOrDefault(ModDataComponents.TIBANNA_AMOUNT, 0);
                int powerPack = stack.getOrDefault(ModDataComponents.TIBANNA_AMOUNT, 0);

                if (tibannaCapsule > 0 && powerPack < maxTibanna) {
                    stack.set(ModDataComponents.TIBANNA_AMOUNT, powerPack + 1);
                    mainHandStack.set(ModDataComponents.TIBANNA_AMOUNT, tibannaCapsule - 1);

                    player.displayClientMessage(Component.translatable("data.starwarschaincode.transferring_tibanna",(powerPack + 1), maxTibanna)
                            .withStyle(ChatFormatting.GREEN), true);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.get(ModDataComponents.TIBANNA_AMOUNT) != null) {
            tooltipComponents.add(Component.translatable("data.starwarschaincode.tibanna",
                    stack.get(ModDataComponents.TIBANNA_AMOUNT), maxTibanna).withStyle(ChatFormatting.DARK_GREEN));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
