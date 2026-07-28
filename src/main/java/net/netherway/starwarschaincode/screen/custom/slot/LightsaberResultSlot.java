package net.netherway.starwarschaincode.screen.custom.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.screen.custom.LightsaberAssemblerMenu;

public class LightsaberResultSlot extends Slot {

    private final LightsaberAssemblerMenu menu;


    public LightsaberResultSlot(LightsaberAssemblerMenu menu, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.menu = menu;
    }


    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof LightsaberItem
                && !menu.hasAnyPart();
    }

    @Override
    public void set(ItemStack stack) {
        super.set(stack);

        if (!stack.isEmpty() && stack.getItem() instanceof LightsaberItem) {
            menu.beginEditing(stack);
        }
    }


    @Override
    public boolean mayPickup(Player player) {
        return hasItem();
    }


    @Override
    public void onTake(Player player, ItemStack stack) {

        super.onTake(player, stack);

        if(!player.level().isClientSide()) {
            menu.consumeParts();
        }
    }
}