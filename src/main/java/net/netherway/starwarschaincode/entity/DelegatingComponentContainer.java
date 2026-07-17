package net.netherway.starwarschaincode.entity;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Container "proxy" cuja referência nunca muda, mas que delega pra um ShipComponentInventory trocável em runtime. */
public class DelegatingComponentContainer implements Container {

    private ShipComponentInventory delegate; // null = nenhuma nave selecionada

    public void setDelegate(ShipComponentInventory delegate) {
        this.delegate = delegate;
    }

    public ShipComponentInventory getDelegate() {
        return delegate;
    }

    @Override
    public int getContainerSize() {
        return ShipInventoryData.MAX_SLOTS; // tamanho fixo pra manter os slots do menu estáveis
    }

    @Override
    public boolean isEmpty() {
        return delegate == null || delegate.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (delegate == null || slot >= delegate.getContainerSize()) return ItemStack.EMPTY;
        return delegate.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (delegate == null || slot >= delegate.getContainerSize()) return ItemStack.EMPTY;
        return delegate.removeItem(slot, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (delegate == null || slot >= delegate.getContainerSize()) return ItemStack.EMPTY;
        return delegate.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (delegate != null && slot < delegate.getContainerSize()) {
            delegate.setItem(slot, stack);
        }
    }

    public boolean canPlaceItem(int slot, ItemStack stack) {
        return delegate != null && slot < delegate.getContainerSize() && delegate.canPlaceItem(slot, stack);
    }

    public boolean isSlotActive(int slot) {
        return delegate != null && slot < delegate.getContainerSize();
    }

    @Override
    public void setChanged() {
        if (delegate != null) delegate.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        if (delegate != null) delegate.clearContent();
    }
}