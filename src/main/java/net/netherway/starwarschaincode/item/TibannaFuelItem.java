package net.netherway.starwarschaincode.item;

import net.minecraft.world.item.ItemStack;

public interface TibannaFuelItem {

    int getTibannaAmount(ItemStack stack);

    int getMaxTibanna(ItemStack stack);
}