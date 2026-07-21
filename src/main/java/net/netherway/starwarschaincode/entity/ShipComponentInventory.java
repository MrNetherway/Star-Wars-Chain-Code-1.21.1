package net.netherway.starwarschaincode.entity;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ShipComponentInventory implements Container {

    private final ShipType shipType;
    private final ShipInventoryData data;

    public ShipComponentInventory(ShipType shipType, ShipInventoryData data) {
        this.shipType = shipType;
        this.data = data;
    }

    public int findSlotByType(ShipType.ComponentType type) {
        for (int i = 0; i < shipType.componentSlots().size(); i++) {
            if (shipType.componentSlots().get(i).type() == type) {
                return i;
            }
        }
        return -1;
    }

    public ShipType.ComponentSlot getSlotDefinition(int index) {
        return shipType.componentSlots().get(index);
    }

    /** Checa se todos os slots obrigatórios estão preenchidos (nave pronta pra uso) */
    public boolean isFullyEquipped() {
        for (int i = 0; i < shipType.componentSlots().size(); i++) {
            ShipType.ComponentSlot slot = shipType.componentSlots().get(i);
            if (slot.required() && data.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= shipType.componentSlots().size()) return false;
        ShipType.ComponentSlot def = getSlotDefinition(slot);
        return stack.is(def.type().tag());
    }

    // --- Container: delega tudo pro ShipInventoryData, mas limitado ao tamanho real da nave ---

    @Override
    public int getContainerSize() {
        return shipType.componentSlots().size();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!data.get(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return data.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack current = data.get(slot);
        if (current.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = current.split(count);
        if (current.isEmpty()) {
            data.set(slot, ItemStack.EMPTY);
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack current = data.get(slot);
        data.set(slot, ItemStack.EMPTY);
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        data.set(slot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1; // componentes são únicos por slot
    }

    @Override
    public void setChanged() {
        // ShipInventoryData é referência direta ao attachment, já persiste sozinho
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public boolean isSlotActive(int slot) {
        return slot >= 0 && slot < shipType.componentSlots().size();
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            data.set(i, ItemStack.EMPTY);
        }
    }
}