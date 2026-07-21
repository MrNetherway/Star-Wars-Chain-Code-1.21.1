package net.netherway.starwarschaincode.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.item.TibannaFuelItem;

import java.util.List;

public class TibannaGasCapsuleItem extends Item implements TibannaFuelItem {
    private int maxTibanna = 100;

    public TibannaGasCapsuleItem(Properties properties) {
        super(properties);
    }

    // --- Implementação da interface ---

    @Override
    public int getTibannaAmount(ItemStack stack) {
        Integer current = stack.get(ModDataComponents.TIBANNA_AMOUNT);
        return current == null ? 0 : current;
    }

    @Override
    public int getMaxTibanna(ItemStack stack) {
        return maxTibanna; // ignora a stack, usa o campo da instância
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(stack.get(ModDataComponents.TIBANNA_AMOUNT) == null) {
            tooltipComponents.add(Component.translatable("data.starwarschaincode.null_tibanna").withStyle(ChatFormatting.DARK_RED));
            return;
        }

        tooltipComponents.add(Component.translatable("data.starwarschaincode.tibanna",
                stack.get(ModDataComponents.TIBANNA_AMOUNT), maxTibanna).withStyle(ChatFormatting.DARK_GREEN));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}