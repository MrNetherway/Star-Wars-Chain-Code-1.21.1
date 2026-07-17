package net.netherway.starwarschaincode.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.netherway.starwarschaincode.component.ModAttachments;
import net.netherway.starwarschaincode.entity.ModShipTypes;
import net.netherway.starwarschaincode.entity.ShipComponentInventory;
import net.netherway.starwarschaincode.entity.ShipInventoryData;
import net.netherway.starwarschaincode.entity.ShipType;
import org.jetbrains.annotations.Nullable;

public class ShipEntity extends Entity {

    private static final EntityDataAccessor<String> SHIP_TYPE_ID =
            SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.STRING);

    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputUp;
    private boolean inputDown;

    // cache resolvido a partir do shipTypeId sincronizado, evita lookup no map todo tick
    private ShipType cachedType;

    public ShipEntity(EntityType<? extends ShipEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SHIP_TYPE_ID, ModShipTypes.Z95_HEADHUNTER.id().toString()); // default temporário
    }

    @Override
    public net.minecraft.world.entity.LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof net.minecraft.world.entity.LivingEntity living ? living : null;
    }

    @Override
    public boolean canAddPassenger(net.minecraft.world.entity.Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public void setShipType(ShipType type) {
        this.entityData.set(SHIP_TYPE_ID, type.id().toString());
        this.cachedType = type;
        this.setBoundingBox(this.makeBoundingBox());
    }

    public ShipType getShipType() {
        if (cachedType == null || !cachedType.id().toString().equals(this.entityData.get(SHIP_TYPE_ID))) {
            cachedType = ModShipTypes.byId(ResourceLocation.parse(this.entityData.get(SHIP_TYPE_ID)));
        }
        return cachedType;
    }

    @Override
    public EntityDimensions getDimensions(net.minecraft.world.entity.Pose pose) {
        ShipType type = getShipType();
        if (type == null) return super.getDimensions(pose);
        return EntityDimensions.scalable(type.hitboxWidth(), type.hitboxHeight());
    }

    public ShipComponentInventory getComponentInventory() {
        ShipInventoryData data = this.getData(ModAttachments.SHIP_INVENTORY);
        return new ShipComponentInventory(getShipType(), data);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            if (this.getFirstPassenger() == null) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean isControlledByLocalInstance() {
        Entity controller = this.getControllingPassenger();
        return controller instanceof Player player && player.isLocalPlayer();
    }

    public void setInput(boolean left, boolean right, boolean up, boolean down) {
        this.inputLeft = left;
        this.inputRight = right;
        this.inputUp = up;
        this.inputDown = down;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isControlledByLocalInstance() && this.getControllingPassenger() != null) {
            this.controlShip();
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void controlShip() {
        ShipType type = getShipType();
        if (type == null) return;

        float yaw = this.getYRot();
        if (inputLeft) yaw += type.turnSpeed();
        if (inputRight) yaw -= type.turnSpeed();
        this.setYRot(yaw);

        Vec3 motion = this.getDeltaMovement();
        double speed = motion.horizontalDistance();

        if (inputUp) {
            speed = Math.min(speed + type.acceleration(), type.maxSpeed());
        } else if (inputDown) {
            speed = Math.max(speed - type.acceleration(), -type.maxSpeed() * 0.5);
        } else {
            speed *= type.friction();
            if (Math.abs(speed) < 0.005) speed = 0;
        }

        double rad = Math.toRadians(yaw);
        double dx = -Math.sin(rad) * speed;
        double dz = Math.cos(rad) * speed;

        this.setDeltaMovement(dx, motion.y, dz);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        ShipType type = getShipType();
        if (type != null) {
            tag.putString("ShipType", type.id().toString());
        }
        // inventário de componentes entra aqui depois (attachment)
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("ShipType")) {
            ShipType type = ModShipTypes.byId(ResourceLocation.parse(tag.getString("ShipType")));
            if (type != null) {
                this.setShipType(type);
            }
        }
    }
}