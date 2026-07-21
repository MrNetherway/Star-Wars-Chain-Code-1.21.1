package net.netherway.starwarschaincode.screen.custom;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.netherway.starwarschaincode.entity.ShipComponentInventory;
import net.netherway.starwarschaincode.entity.ShipInventoryData;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.screen.ModMenuTypes;

public class ShipComponentMenu extends AbstractContainerMenu {

    private static final int[][] COMPONENT_SLOT_POSITIONS = {
            {29, 27}, {54, 27}, {79, 27}, {104, 27}, {129, 27},
            {79, 57}, {116, 50}, {134, 50},
    };

    private final ShipEntity ship;

    public ShipComponentMenu(int windowId, Inventory inv, int shipEntityId) {
        super(ModMenuTypes.SHIP_COMPONENT_MENU.get(), windowId);

        Entity entity = inv.player.level().getEntity(shipEntityId);
        this.ship = entity instanceof ShipEntity s ? s : null;


        // SEMPRE adiciona ShipInventoryData.MAX_SLOTS slots, com ou sem ship encontrada
        Container inventory = (ship != null) ? ship.getComponentInventory() : new SimpleContainer(ShipInventoryData.MAX_SLOTS);

        for (int i = 0; i < ShipInventoryData.MAX_SLOTS; i++) {
            int slotIndex = i;
            int x = COMPONENT_SLOT_POSITIONS[i][0];
            int y = COMPONENT_SLOT_POSITIONS[i][1];

            this.addSlot(new Slot(inventory, i, x, y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    if (!(inventory instanceof ShipComponentInventory sci)) return false;
                    return sci.canPlaceItem(slotIndex, stack);
                }

                @Override
                public boolean isActive() {
                    if (inventory instanceof ShipComponentInventory sci) {
                        return sci.isSlotActive(slotIndex);
                    }
                    return false;
                }
            });
        }

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return ship != null && ship.isAlive();
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i)
            for (int l = 0; l < 9; ++l)
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }
}