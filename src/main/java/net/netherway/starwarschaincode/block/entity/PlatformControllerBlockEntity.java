package net.netherway.starwarschaincode.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.netherway.starwarschaincode.block.PlatformControllerBlock;
import net.netherway.starwarschaincode.block.entity.renderer.ModBlockEntities;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.entity.ModShipTypes;
import net.netherway.starwarschaincode.entity.ShipType;
import net.netherway.starwarschaincode.item.ModItems;

import java.util.List;

public class PlatformControllerBlockEntity extends BlockEntity {

    // Slot único: blueprint de casco
    private final SimpleContainer hullSlot = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            onHullSlotChanged();
        }
    };

    public PlatformControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLATFORM_CONTROLLER_BE.get(), pos, state);
    }

    public SimpleContainer getHullSlot() {
        return hullSlot;
    }

    private boolean dismantling = false;

    public void onFillerBroken(BlockPos brokenFillerPos) {
        if (level == null || dismantling) return;
        dismantling = true;

        int size = PlatformControllerBlock.SIZE;
        for (int dx = 0; dx < size; dx++) {
            for (int dz = 0; dz < size; dz++) {
                BlockPos pos = worldPosition.offset(dx, 0, dz);
                if (pos.equals(worldPosition) || pos.equals(brokenFillerPos)) continue;
                level.removeBlock(pos, false);
            }
        }
        level.removeBlock(worldPosition, false);
    }

    public void breakMultiblock() {
        if (level == null || dismantling) return;
        dismantling = true;

        int size = PlatformControllerBlock.SIZE;
        for (int dx = 0; dx < size; dx++) {
            for (int dz = 0; dz < size; dz++) {
                BlockPos pos = worldPosition.offset(dx, 0, dz);
                if (pos.equals(worldPosition)) continue;
                level.removeBlock(pos, false);
            }
        }
    }

    /** Chamado quando o item do slot de casco muda */
    private void onHullSlotChanged() {
        if (level == null || level.isClientSide) return;

        ItemStack stack = hullSlot.getItem(0);
        if (stack.isEmpty()) return;

        ShipType type = resolveShipTypeFromBlueprint(stack);
        if (type == null) return; // item não é uma blueprint de casco reconhecida

        spawnShip(type);
        hullSlot.setItem(0, ItemStack.EMPTY); // consome a blueprint
    }

    private ShipType resolveShipTypeFromBlueprint(ItemStack stack) {
        if (stack.is(ModItems.Z_95_HEADHUNTER_BLUEPRINT.get())) {
            return ModShipTypes.Z95_HEADHUNTER;
        }
        // próximas naves: else if (stack.is(ModItems.X_BLUEPRINT.get())) return ModShipTypes.X;
        return null;
    }

    private void spawnShip(ShipType type) {
        if (level == null) return;

        ShipEntity ship = new ShipEntity(net.netherway.starwarschaincode.entity.ModEntities.SHIP.get(), level);
        ship.setShipType(type);

        double half = PlatformControllerBlock.SIZE / 2.0;
        double x = this.worldPosition.getX() + half;
        double y = this.worldPosition.getY() + 0.125;
        double z = this.worldPosition.getZ() + half;
        ship.setPos(x, y, z);

        level.addFreshEntity(ship);
    }

    public List<ShipEntity> getShipsOnPlatform() {
        if (level == null) return List.of();

        int size = PlatformControllerBlock.SIZE;
        AABB scanArea = new AABB(
                worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                worldPosition.getX() + size, worldPosition.getY() + 4, worldPosition.getZ() + size
        );

        List<ShipEntity> ships = new java.util.ArrayList<>(level.getEntitiesOfClass(ShipEntity.class, scanArea));
        ships.sort(java.util.Comparator.comparing(ShipEntity::getUUID));
        return ships;
    }

    public void openMenu(Player player) {
        if (level == null || level.isClientSide) return;
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (windowId, inv, p) -> new net.netherway.starwarschaincode.screen.custom.PlatformMenu(windowId, inv, this.worldPosition),
                    net.minecraft.network.chat.Component.translatable("menu.starwarschaincode.platform")
            ), this.worldPosition);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ItemStack hullStack = hullSlot.getItem(0);
        if (!hullStack.isEmpty()) {
            CompoundTag hullTag = new CompoundTag();
            hullStack.save(provider, hullTag);
            tag.put("HullSlot", hullTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("HullSlot")) {
            ItemStack.parse(provider, tag.getCompound("HullSlot")).ifPresent(stack -> hullSlot.setItem(0, stack));
        }
    }
}