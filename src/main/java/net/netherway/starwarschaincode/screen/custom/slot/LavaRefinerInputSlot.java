package net.netherway.starwarschaincode.screen.custom.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.netherway.starwarschaincode.item.ModItems;

public class LavaRefinerInputSlot extends SlotItemHandler {

    public LavaRefinerInputSlot(IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(ModItems.RAW_DOONIUM) ||
                stack.is(ModItems.RAW_BAUXITE) ||
                stack.is(ModItems.RAW_QUADANIUM) ||
                stack.is(ModItems.NON_REFINED_PLASTOID_COMPOUND);
    }
}