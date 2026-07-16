package net.netherway.starwarschaincode.screen.custom;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.item.ModItems;
import net.netherway.starwarschaincode.recipe.BlueprintRecipe;
import net.netherway.starwarschaincode.recipe.BlueprintRecipeInput;
import net.netherway.starwarschaincode.recipe.ModRecipes;
import net.netherway.starwarschaincode.screen.ModMenuTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlueprintBuilderMenu extends AbstractContainerMenu {

    public static final int GRID_WIDTH = 4;
    public static final int GRID_HEIGHT = 4;
    public static final int GRID_SIZE = GRID_WIDTH * GRID_HEIGHT;

    private final Container craftGrid = new SimpleContainer(GRID_SIZE) {
        @Override
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };
    private final Container catalystSlotContainer = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };
    private final ResultContainer resultContainer = new ResultContainer();
    private final ContainerLevelAccess access;

    // construtor usado no client (chamado pelo IContainerFactory ao abrir via rede)
    public BlueprintBuilderMenu(int containerId, Inventory playerInventory, net.minecraft.network.RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    // construtor usado no server (aberto pelo Block)
    public BlueprintBuilderMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenuTypes.BLUEPRINT_BUILDER_MENU.get(), containerId);
        this.access = access;

        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                int index = col + row * GRID_WIDTH;
                addSlot(new Slot(craftGrid, index, 13 + col * 18, 6 + row * 18));
            }
        }

        addSlot(new Slot(catalystSlotContainer, 0, 106, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.PEN.get());
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        addSlot(new Slot(resultContainer, 0, 154, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                ItemStack pen = catalystSlotContainer.getItem(0);
                if (!pen.isEmpty()) {
                    pen.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
                for (int i = 0; i < craftGrid.getContainerSize(); i++) {
                    craftGrid.removeItem(i, 1);
                }
                craftGrid.setChanged();
                super.onTake(player, stack);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        access.execute((level, pos) -> {
            if (level.isClientSide) return;

            if (catalystSlotContainer.getItem(0).isEmpty()) {
                resultContainer.setItem(0, ItemStack.EMPTY);
                broadcastChanges();
                return;
            }

            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < GRID_SIZE; i++) {
                items.add(craftGrid.getItem(i));
            }
            BlueprintRecipeInput input = new BlueprintRecipeInput(items, GRID_WIDTH, GRID_HEIGHT);

            Optional<RecipeHolder<BlueprintRecipe>> match =
                    level.getRecipeManager().getRecipeFor(ModRecipes.BLUEPRINT_TYPE.get(), input, level);

            resultContainer.setItem(0, match
                    .map(holder -> holder.value().assemble(input, level.registryAccess()))
                    .orElse(ItemStack.EMPTY));
            broadcastChanges();
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // TODO: implementar shift-click
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.BLUEPRINT_BUILDER.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, pos) -> {
            if (!level.isClientSide) {
                clearContainer(player, craftGrid);
                clearContainer(player, catalystSlotContainer);
            }
        });
    }
}