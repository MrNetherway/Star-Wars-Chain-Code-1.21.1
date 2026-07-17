package net.netherway.starwarschaincode.screen.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.netherway.starwarschaincode.block.entity.PlatformControllerBlockEntity;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.entity.DelegatingComponentContainer;
import net.netherway.starwarschaincode.entity.ShipInventoryData;
import net.netherway.starwarschaincode.screen.ModMenuTypes;

import java.util.List;

public class PlatformMenu extends AbstractContainerMenu {

    private static final int COMPONENT_SLOTS_START = 1;

    private final PlatformControllerBlockEntity controller;
    private final Player player;
    private final DelegatingComponentContainer componentContainer = new DelegatingComponentContainer();

    private List<ShipEntity> availableShips = List.of();
    private int selectedShipIndex = -1;

    public PlatformMenu(int windowId, Inventory inv, BlockPos controllerPos) {
        super(ModMenuTypes.PLATFORM_MENU.get(), windowId);
        this.player = inv.player;
        this.controller = (PlatformControllerBlockEntity) inv.player.level().getBlockEntity(controllerPos);

        // Slot 0: casco — só ativo na tela principal
        this.addSlot(new Slot(controller.getHullSlot(), 0, 26, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }

            @Override
            public boolean isActive() {
                return viewMode == ViewMode.MAIN;
            }
        });

// Slots de componente — só ativos na tela de nave
        for (int i = 0; i < ShipInventoryData.MAX_SLOTS; i++) {
            int slotIndex = i;
            this.addSlot(new Slot(componentContainer, i, 26 + (i % 4) * 18, 50 + (i / 4) * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return componentContainer.canPlaceItem(slotIndex, stack);
                }

                @Override
                public boolean isActive() {
                    return viewMode == ViewMode.SHIP_COMPONENTS && componentContainer.isSlotActive(slotIndex);
                }
            });
        }

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        refreshAvailableShips();
    }

    public void refreshAvailableShips() {
        if (controller == null) return;
        this.availableShips = controller.getShipsOnPlatform();

        if (selectedShipIndex >= 0 && selectedShipIndex >= availableShips.size()) {
            selectShip(-1);
        }
    }

    public List<ShipEntity> getAvailableShips() {
        return availableShips;
    }

    public int getSelectedShipIndex() {
        return selectedShipIndex;
    }

    public enum ViewMode { MAIN, SHIP_COMPONENTS }

    private ViewMode viewMode = ViewMode.MAIN;

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void selectShip(int index) {
        // clicar na mesma nave de novo = voltar pra tela principal (toggle)
        if (viewMode == ViewMode.SHIP_COMPONENTS && this.selectedShipIndex == index) {
            this.viewMode = ViewMode.MAIN;
            this.selectedShipIndex = -1;
            componentContainer.setDelegate(null);
            this.broadcastChanges();
            return;
        }

        if (index < 0 || index >= availableShips.size()) {
            this.viewMode = ViewMode.MAIN;
            this.selectedShipIndex = -1;
            componentContainer.setDelegate(null);
        } else {
            this.viewMode = ViewMode.SHIP_COMPONENTS;
            this.selectedShipIndex = index;
            ShipEntity ship = availableShips.get(index);
            componentContainer.setDelegate(ship.getComponentInventory());
        }
        this.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // TODO: shift-click entre inventário do player e slots de componente
    }

    @Override
    public boolean stillValid(Player player) {
        return controller != null && !controller.isRemoved()
                && player.distanceToSqr(controller.getBlockPos().getX(), controller.getBlockPos().getY(), controller.getBlockPos().getZ()) < 64;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}