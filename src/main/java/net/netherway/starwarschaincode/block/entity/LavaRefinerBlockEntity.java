package net.netherway.starwarschaincode.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.netherway.starwarschaincode.block.custom.LavaRefinerBlock;
import net.netherway.starwarschaincode.block.entity.renderer.ModBlockEntities;
import net.netherway.starwarschaincode.recipe.LavaRefinerRecipe;
import net.netherway.starwarschaincode.recipe.LavaRefinerRecipeInput;
import net.netherway.starwarschaincode.recipe.ModRecipes;
import net.netherway.starwarschaincode.screen.custom.LavaRefinerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.netherway.starwarschaincode.block.custom.LavaRefinerBlock.HAS_LAVA;

public class LavaRefinerBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };



    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int LAVA_SLOT = 2;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
    private int lava = 0;
    private int maxLava = 100;

    public LavaRefinerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LAVA_REFINER_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> LavaRefinerBlockEntity.this.progress;
                    case 1 -> LavaRefinerBlockEntity.this.maxProgress;
                    case 2 -> LavaRefinerBlockEntity.this.lava;
                    case 3 -> LavaRefinerBlockEntity.this.maxLava;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:LavaRefinerBlockEntity.this.progress = value;
                    case 1:LavaRefinerBlockEntity.this.maxProgress = value;
                    case 2:LavaRefinerBlockEntity.this.lava = value;
                    case 3:LavaRefinerBlockEntity.this.maxLava = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.starwarschaincode.lava_refiner");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new LavaRefinerMenu(containerId, playerInventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        if(lava>0) {
            level.setBlock(this.worldPosition, Blocks.LAVA.defaultBlockState(),3);
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("lava_refiner.progress", progress);
        tag.putInt("lava_refiner.max_progress", maxProgress);
        tag.putInt("lava_refiner.lava", lava);
        tag.putInt("lava_refiner.max_lava", maxLava);

        super.saveAdditional(tag,registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("lava_refiner.progress");
        maxProgress = tag.getInt("lava_refiner.max_progress");
        lava = tag.getInt("lava_refiner.lava");
        maxLava = tag.getInt("lava_refiner.max_lava");
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        consumeLavaBucket();
        if(!level.isClientSide) {
            if(lava > 0) {
                level.setBlockAndUpdate(blockPos, blockState.setValue(HAS_LAVA, true));
            } else {
                level.setBlockAndUpdate(blockPos, blockState.setValue(HAS_LAVA, false));
            }
        }
        if(hasRecipe()) {
            increaseCraftingProgress();
            setChanged(level, blockPos, blockState);

            if(hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void consumeLavaBucket() {
        ItemStack stack = itemHandler.getStackInSlot(LAVA_SLOT);
        if(stack.getItem() != Items.LAVA_BUCKET || lava >= 100 || lava+10 > 100)
            return;
        itemHandler.extractItem(LAVA_SLOT, 1, false);
        itemHandler.setStackInSlot(LAVA_SLOT, new ItemStack(Items.BUCKET));
        increaseLava();
    }

    private void craftItem() {
        Optional<RecipeHolder<LavaRefinerRecipe>> recipe = getCurrentRecipe();
        ItemStack output = recipe.get().value().output();

        itemHandler.extractItem(INPUT_SLOT, 1, false);
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(),
                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
        decreaseLava();
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 72;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private void increaseLava(){ lava+=10; }

    private void decreaseLava(){ lava-=2; }

    private boolean hasRecipe() {
        Optional<RecipeHolder<LavaRefinerRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty() || lava-2 < 0)
            return false;

        ItemStack output = recipe.get().value().output();
        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    private Optional<RecipeHolder<LavaRefinerRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.LAVA_REFINER_TYPE.get(), new LavaRefinerRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT)), level);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


}
