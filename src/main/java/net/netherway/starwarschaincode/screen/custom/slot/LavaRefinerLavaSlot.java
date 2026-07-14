package net.netherway.starwarschaincode.screen.custom.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LavaRefinerLavaSlot extends SlotItemHandler {
    public LavaRefinerLavaSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(Items.LAVA_BUCKET);
    }
}
