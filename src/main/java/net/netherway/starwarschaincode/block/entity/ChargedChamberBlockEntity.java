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
import net.neoforged.neoforge.items.ItemStackHandler;
import net.netherway.starwarschaincode.block.entity.renderer.ModBlockEntities;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.item.ModItems;
import net.netherway.starwarschaincode.recipe.*;
import net.netherway.starwarschaincode.screen.custom.ChargedChamberMenu;
import net.netherway.starwarschaincode.screen.custom.LavaRefinerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ChargedChamberBlockEntity extends BlockEntity implements MenuProvider {
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
    private static final int ENERGY_SLOT = 2;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
    private int energy = 0;
    private int maxEnergy = 100;

    public ChargedChamberBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CHARGED_CHAMBER_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ChargedChamberBlockEntity.this.progress;
                    case 1 -> ChargedChamberBlockEntity.this.maxProgress;
                    case 2 -> ChargedChamberBlockEntity.this.energy;
                    case 3 -> ChargedChamberBlockEntity.this.maxEnergy;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:ChargedChamberBlockEntity.this.progress = value;
                    case 1:ChargedChamberBlockEntity.this.maxProgress = value;
                    case 2:ChargedChamberBlockEntity.this.energy = value;
                    case 3:ChargedChamberBlockEntity.this.maxEnergy = value;
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
        return Component.translatable("block.starwarschaincode.charged_chamber");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ChargedChamberMenu(containerId, playerInventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("charged_chamber.progress", progress);
        tag.putInt("charged_chamber.max_progress", maxProgress);
        tag.putInt("charged_chamber.energy", energy);
        tag.putInt("charged_chamber.max_energy", maxEnergy);

        super.saveAdditional(tag,registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("charged_chamber.progress");
        maxProgress = tag.getInt("charged_chamber.max_progress");
        energy = tag.getInt("charged_chamber.energy");
        maxEnergy = tag.getInt("charged_chamber.max_energy");
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        consumeEnergy();
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

    private void consumeEnergy() {
        ItemStack stack = itemHandler.getStackInSlot(ENERGY_SLOT);
        if(energy >= 100 || !stack.is(ModItems.PORTABLE_SOLAR_COLLECTOR))
            return;
        Integer solar = stack.getOrDefault(ModDataComponents.SOLAR_AMOUNT, 0);

        if(solar <= 0) { return; }

        stack.set(ModDataComponents.SOLAR_AMOUNT, solar - 1);
        increaseEnergy();
    }

    private void craftItem() {
        Optional<RecipeHolder<ChargedChamberRecipe>> recipe = getCurrentRecipe();
        ItemStack output = recipe.get().value().output();

        itemHandler.extractItem(INPUT_SLOT, 1, false);
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(),
                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
        decreaseEnergy();
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

    private void increaseEnergy(){ energy+=1; }

    private void decreaseEnergy(){ energy-=10; }

    private boolean hasRecipe() {
        Optional<RecipeHolder<ChargedChamberRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty() || energy-1 < 0)
            return false;

        ItemStack output = recipe.get().value().output();
        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    private Optional<RecipeHolder<ChargedChamberRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.CHARGED_CHAMBER_TYPE.get(), new ChargedChamberRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT)), level);
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
