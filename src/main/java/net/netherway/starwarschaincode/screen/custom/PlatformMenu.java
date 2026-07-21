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

    private int serverTickCounter = 0;
    private static final int SERVER_REFRESH_INTERVAL = 20; // 1s

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
    }

    private static final int[][] COMPONENT_SLOT_POSITIONS = {
            {29, 27},   // slot 0
            {54, 27},   // slot 1
            {79, 27},   // slot 2
            {104, 27},   // slot 3
            {129, 27},   // slot 4
            {79, 57},   // slot 5 (energia) - posição diferente
            {116, 50},  // slot 6
            {134, 50},  // slot 7
    };

    public PlatformMenu(int windowId, Inventory inv, BlockPos controllerPos) {
        super(ModMenuTypes.PLATFORM_MENU.get(), windowId);
        this.player = inv.player;
        this.controller = (PlatformControllerBlockEntity) inv.player.level().getBlockEntity(controllerPos);

        // Slot 0: casco — só ativo na tela principal
        this.addSlot(new Slot(controller.getHullSlot(), 0, 79, 33
        ) {
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
            int x = COMPONENT_SLOT_POSITIONS[i][0];
            int y = COMPONENT_SLOT_POSITIONS[i][1];

            this.addSlot(new Slot(componentContainer, i, x, y) {
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

    public void selectShipById(java.util.UUID shipId) {
        if (shipId == null) {
            deselectShip();
            return;
        }

        ShipEntity target = availableShips.stream()
                .filter(s -> s.getUUID().equals(shipId))
                .findFirst()
                .orElse(null);

        if (target == null) {
            // pode estar desatualizado (ex: nave spawnou depois da criação do menu) — tenta um refresh antes de desistir
            refreshAvailableShips();
            target = availableShips.stream()
                    .filter(s -> s.getUUID().equals(shipId))
                    .findFirst()
                    .orElse(null);
        }

        if (target == null) return; // realmente não existe

        if (viewMode == ViewMode.SHIP_COMPONENTS && shipId.equals(selectedShipId)) {
            deselectShip();
            return;
        }

        this.viewMode = ViewMode.SHIP_COMPONENTS;
        this.selectedShipId = shipId;
        componentContainer.setDelegate(target.getComponentInventory());
        this.broadcastChanges();
    }

    public void refreshAvailableShips() {
        if (controller == null) return;
        this.availableShips = controller.getShipsOnPlatform();

        if (selectedShipId != null) {
            ShipEntity stillThere = availableShips.stream()
                    .filter(s -> s.getUUID().equals(selectedShipId))
                    .findFirst()
                    .orElse(null);

            if (stillThere == null) {
                deselectShip();
            } else {
                componentContainer.setDelegate(stillThere.getComponentInventory());
            }
        }
    }

    public List<ShipEntity> getAvailableShips() {
        return availableShips;
    }

    public int getSelectedShipIndex() {
        if (selectedShipId == null) return -1;
        for (int i = 0; i < availableShips.size(); i++) {
            if (availableShips.get(i).getUUID().equals(selectedShipId)) return i;
        }
        return -1;
    }

    private java.util.UUID selectedShipId = null;

    public enum ViewMode { MAIN, SHIP_COMPONENTS }

    private ViewMode viewMode = ViewMode.MAIN;

    public ViewMode getViewMode() {
        return viewMode;
    }

    private void deselectShip() {
        this.viewMode = ViewMode.MAIN;
        this.selectedShipId = null;
        componentContainer.setDelegate(null);
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