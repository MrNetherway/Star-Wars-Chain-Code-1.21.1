package net.netherway.starwarschaincode.screen.custom.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.netherway.starwarschaincode.item.ModItems;

public class ChargedChamberEnergySlot  extends SlotItemHandler {
    public ChargedChamberEnergySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(ModItems.PORTABLE_SOLAR_COLLECTOR);
    }
}
