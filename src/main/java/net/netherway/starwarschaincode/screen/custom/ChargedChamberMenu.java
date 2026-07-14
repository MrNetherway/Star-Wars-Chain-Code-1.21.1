package net.netherway.starwarschaincode.screen.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.block.entity.ChargedChamberBlockEntity;
import net.netherway.starwarschaincode.block.entity.LavaRefinerBlockEntity;
import net.netherway.starwarschaincode.screen.ModMenuTypes;
import net.netherway.starwarschaincode.screen.custom.slot.*;

public class ChargedChamberMenu extends AbstractContainerMenu {
    public final ChargedChamberBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;



    public ChargedChamberMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public ChargedChamberMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.CHARGED_CHAMBER_MENU.get(), containerId);
        this.blockEntity = ((ChargedChamberBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        this.addSlot(new ChargedChamberInputSlot(blockEntity.itemHandler, 0, 57, 26));
        this.addSlot(new ChargedChamberResultSlot(blockEntity.itemHandler, 1, 103, 26));
        this.addSlot(new ChargedChamberEnergySlot(blockEntity.itemHandler, 2, 80, 51));

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }
    public boolean addedLava() {return data.get(2) > 0;}

    public int getScaledArrowProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 24;

        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }

    public int getScaledBarProgress() {
        int energy = this.data.get(2);
        int maxEnergy = this.data.get(3);
        int barPixelSize = 41;
        return (energy*barPixelSize)/maxEnergy;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_SLOT_COUNT = 3;

    private static final int VANILLA_FIRST_SLOT_INDEX =
            TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_SLOT_COUNT = 36;

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);

        if (sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        // Máquina -> Inventário
        if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {

            if (!moveItemStackTo(sourceStack,
                    VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
                    false)) {
                return ItemStack.EMPTY;
            }

        }
        // Inventário -> Máquina
        else if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {

            if (!moveItemStackTo(sourceStack,
                    TE_INVENTORY_FIRST_SLOT_INDEX,
                    TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT,
                    false)) {
                return ItemStack.EMPTY;
            }

        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);

        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.CHARGED_CHAMBER.get());
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
