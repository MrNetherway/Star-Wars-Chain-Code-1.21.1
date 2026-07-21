package net.netherway.starwarschaincode.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.netherway.starwarschaincode.component.ModAttachments;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.entity.ModShipTypes;
import net.netherway.starwarschaincode.entity.ShipComponentInventory;
import net.netherway.starwarschaincode.entity.ShipInventoryData;
import net.netherway.starwarschaincode.entity.ShipType;
import net.netherway.starwarschaincode.item.TibannaFuelItem;
import net.netherway.starwarschaincode.item.custom.TibannaGasCapsuleItem;
import net.netherway.starwarschaincode.screen.custom.ShipComponentMenu;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class ShipEntity extends Entity {

    private static final EntityDataAccessor<String> SHIP_TYPE_ID =
            SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Float> FORWARD_SPEED =
            SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ORIENT_X =
            SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ORIENT_Y =
            SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ORIENT_Z =
            SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ORIENT_W =
            SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FLIGHT_READY =
            SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TIBANNA_CURRENT =
            SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TIBANNA_MAX =
            SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.INT);

    // --- Input vindo do client ---
    private boolean rollLeft, rollRight, thrustForward, thrustBackward, ascend, descend;

    // --- Estado de voo ---
    private float forwardSpeed = 0f;
    private float gravityPull = 0f;

    private static final float MAX_ROLL_SPEED = 4.0f;
    private static final float VERTICAL_SPEED = 0.4f;
    private static final float GRAVITY_BRAKE_ACCEL = 0.02f;
    private static final float MAX_GRAVITY_PULL = 0.5f;
    private static final float GRAVITY_RECOVERY = 0.05f;
    private static final float IDLE_GRAVITY_PULL = 0.005f; // puxão constante quando totalmente parado
    private static final float PASSIVE_DRAG = 0.995f; // desaceleração natural ao soltar W (bem lenta)
    private static final float IDLE_DRAG = 0.92f; // desaceleração mais forte ao ficar sem piloto, mas ainda suave
    private static final float BRAKE_DRAG_ACCEL = 0.03f; // desaceleração ativa ao segurar S (mais forte)
    private static final float GROUND_CHECK_DISTANCE = 1.2f;
    private static final float GROUND_STABILIZE_LERP = 0.08f;

    private int tibannaTickCounter = 0;
    private static final int TICKS_PER_SECOND = 100;

    private float currentVerticalSpeed = 0f;
    private static final float VERTICAL_ACCEL = 0.03f; // suavidade da subida/descida

    private static final float NO_FUEL_GRAVITY = 0.04f; // força de queda quando sem tibanna
    private static final float MAX_FALL_SPEED = 1.2f;   // velocidade terminal

    private ShipType cachedType;

    // Orientação atual e do tick anterior (pra interpolação de render/câmera)
    private Quaternionf orientation = new Quaternionf();
    private Quaternionf prevOrientation = new Quaternionf();

    public ShipEntity(EntityType<? extends ShipEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SHIP_TYPE_ID, ModShipTypes.Z95_HEADHUNTER.id().toString());
        builder.define(ORIENT_X, 0f);
        builder.define(ORIENT_Y, 0f);
        builder.define(ORIENT_Z, 0f);
        builder.define(ORIENT_W, 1f);
        builder.define(FORWARD_SPEED, 0f);
        builder.define(FLIGHT_READY, false);
        builder.define(TIBANNA_CURRENT, 0);
        builder.define(TIBANNA_MAX, 0);
    }

    // ================== Orientação (quaternion local, sem Euler) ==================

    private void syncOrientation() {
        this.entityData.set(ORIENT_X, orientation.x);
        this.entityData.set(ORIENT_Y, orientation.y);
        this.entityData.set(ORIENT_Z, orientation.z);
        this.entityData.set(ORIENT_W, orientation.w);
    }

    /** Chamado quando os dados sincronizados chegam do server (em passageiros não-controladores) */
    private void loadOrientationFromSyncedData() {
        orientation.set(
                this.entityData.get(ORIENT_X),
                this.entityData.get(ORIENT_Y),
                this.entityData.get(ORIENT_Z),
                this.entityData.get(ORIENT_W)
        );
    }

    public void addLocalPitch(float degrees) {
        if (this.getControllingPassenger() == null) return;
        if (!isFlightReady()) return;
        orientation.rotateX((float) Math.toRadians(degrees));
        orientation.normalize();
        syncOrientation();
    }

    public void addLocalYaw(float degrees) {
        if (this.getControllingPassenger() == null) return;
        if (!isFlightReady()) return;
        orientation.rotateY((float) Math.toRadians(-degrees));
        orientation.normalize();
        syncOrientation();
    }

    public void addLocalRoll(float degrees) {
        if (this.getControllingPassenger() == null) return;
        orientation.rotateZ((float) Math.toRadians(degrees));
        orientation.normalize();
        syncOrientation();
    }

    private boolean computeFlightReady() {
        ShipType type = getShipType();
        if (type == null) return false;
        return getComponentInventory().isFullyEquipped() && hasTibannaFuel();
    }

    /** Orientação interpolada para renderização/câmera suave entre ticks */
    public Quaternionf getOrientation(float partialTick) {
        return new Quaternionf(prevOrientation).slerp(orientation, partialTick);
    }

    public Quaternionf getOrientation() {
        return new Quaternionf(orientation);
    }

    public Vec3 getForwardVector() {
        Vector3f forward = new Vector3f(0, 0, 1);
        orientation.transform(forward);
        return new Vec3(forward.x, forward.y, forward.z);
    }

    // ================== Ship type ==================

    public void setShipType(ShipType type) {
        this.entityData.set(SHIP_TYPE_ID, type.id().toString());
        this.cachedType = type;
        this.refreshDimensions();
    }

    public ShipType getShipType() {
        if (cachedType == null || !cachedType.id().toString().equals(this.entityData.get(SHIP_TYPE_ID))) {
            cachedType = ModShipTypes.byId(ResourceLocation.parse(this.entityData.get(SHIP_TYPE_ID)));
        }
        return cachedType;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        ShipType type = getShipType();
        if (type == null) return super.getDimensions(pose);
        return EntityDimensions.fixed(type.hitboxWidth(), type.hitboxHeight());
    }

    public ShipComponentInventory getComponentInventory() {
        ShipInventoryData data = this.getData(ModAttachments.SHIP_INVENTORY);
        return new ShipComponentInventory(getShipType(), data);
    }

    // ================== Interação / montaria ==================

    @Override
    public float getPickRadius() {
        ShipType type = getShipType();
        if (type == null) return super.getPickRadius();
        // metade do maior lado do hitbox, como margem extra de clique
        return Math.max(type.hitboxWidth(), type.hitboxHeight()) * 0.5f;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                int shipEntityId = this.getId();
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (windowId, inv, p) -> new ShipComponentMenu(windowId, inv, this.getId()),
                        Component.translatable("screen.starwarschaincode.ship_components")
                ), buf -> buf.writeVarInt(shipEntityId));
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (!this.level().isClientSide) {
            if (this.getFirstPassenger() == null) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof LivingEntity living ? living : null;
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        Entity controller = this.getControllingPassenger();
        return controller instanceof Player player && player.isLocalPlayer();
    }

    public void setFlightInput(boolean rollLeft, boolean rollRight, boolean thrustForward,
                               boolean thrustBackward, boolean ascend, boolean descend) {
        if (this.getControllingPassenger() == null) {
            // sem piloto, zera tudo — evita qualquer input "fantasma" residual alimentando idleDrift
            this.rollLeft = this.rollRight = this.thrustForward = this.thrustBackward = this.ascend = this.descend = false;
            return;
        }
        this.rollLeft = rollLeft;
        this.rollRight = rollRight;
        this.thrustForward = thrustForward;
        this.thrustBackward = thrustBackward;
        this.ascend = ascend;
        this.descend = descend;
    }

    // ================== Tick / movimento ==================

    public boolean isFlightReady() {
        return this.entityData.get(FLIGHT_READY);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);

        if (!this.level().isClientSide && passenger instanceof Player) {
            // Força um resync completo da entity pros clients assim que o piloto sai,
            // evitando que a última posição "predita" localmente pelo ex-piloto fique desatualizada
            if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.getChunkSource().broadcastAndSend(this, new net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket(this));
            }
        }
    }

    public int getTibannaGas() {
        return this.entityData.get(TIBANNA_CURRENT);
    }

    public int getMaxTibannaGas() {
        return this.entityData.get(TIBANNA_MAX);
    }

    private void tickTibannaConsumption() {
        if (this.level().isClientSide) return;
        if (!isFlightReady()) return;

        boolean isMoving = forwardSpeed > 0.01f;
        if (!isMoving) {
            tibannaTickCounter = 0; // parado, não acumula consumo
            return;
        }

        tibannaTickCounter++;
        if (tibannaTickCounter < TICKS_PER_SECOND) return;
        tibannaTickCounter = 0;

        ShipType type = getShipType();
        ShipComponentInventory inventory = getComponentInventory();
        int energySlot = inventory.findSlotByType(ShipType.ComponentType.ENERGY);
        if (energySlot < 0) return;

        ItemStack tibannaStack = inventory.getItem(energySlot);
        if (tibannaStack.isEmpty()) return;

        Integer current = tibannaStack.get(ModDataComponents.TIBANNA_AMOUNT);
        if (current == null || current <= 0) return;

        int consumed = Math.max(1, Math.round(current * type.tibannaConsumptionRate()));
        int newAmount = Math.max(0, current - consumed);

        tibannaStack.set(ModDataComponents.TIBANNA_AMOUNT, newAmount);
        inventory.setItem(energySlot, tibannaStack); // garante persistência via attachment
    }

    private void syncTibannaData() {
        if (this.level().isClientSide) return;

        ShipComponentInventory inventory = getComponentInventory();
        int energySlot = inventory.findSlotByType(ShipType.ComponentType.ENERGY);

        int current = 0;
        int max = 0;

        if (energySlot >= 0) {
            ItemStack stack = inventory.getItem(energySlot);
            if (!stack.isEmpty() && stack.getItem() instanceof TibannaFuelItem fuelItem) {
                current = fuelItem.getTibannaAmount(stack);
                max = fuelItem.getMaxTibanna(stack);
            }
        }

        this.entityData.set(TIBANNA_CURRENT, current);
        this.entityData.set(TIBANNA_MAX, max);
    }

    private boolean hasTibannaFuel() {
        ShipComponentInventory inventory = getComponentInventory();
        int energySlot = inventory.findSlotByType(ShipType.ComponentType.ENERGY);
        if (energySlot < 0) return false;

        ItemStack tibannaStack = inventory.getItem(energySlot);
        if (tibannaStack.isEmpty()) return false;

        Integer amount = tibannaStack.get(ModDataComponents.TIBANNA_AMOUNT);
        return amount != null && amount > 0;
    }

    public void zeroMotion() {
        this.forwardSpeed = 0f;
        this.currentVerticalSpeed = 0f;
        this.gravityPull = 0f;
        this.setDeltaMovement(Vec3.ZERO);
        if (!this.level().isClientSide) {
            this.entityData.set(FORWARD_SPEED, 0f);
        }
    }

    @Override
    public void tick() {
        prevOrientation.set(orientation);

        if (!isControlledByLocalInstance()) {
            loadOrientationFromSyncedData();
            forwardSpeed = this.entityData.get(FORWARD_SPEED);
        }

        if (!this.level().isClientSide) {
            this.entityData.set(FLIGHT_READY, computeFlightReady());
            this.syncTibannaData();
        }

        super.tick();

        boolean hasPilot = this.getControllingPassenger() != null;
        boolean canFly = isFlightReady();
        boolean localAuthority = this.isControlledByLocalInstance() || !this.level().isClientSide;

        boolean pilotTraveling = hasPilot && getControllingPassenger() instanceof net.minecraft.world.entity.player.Player p
                && p.getData(net.netherway.starwarschaincode.component.ModAttachments.HYPERSPACE_TRAVEL).traveling;

        if (pilotTraveling) {
            this.zeroMotion(); // trava física total, todo tick, enquanto durar a viagem
        } else if (hasPilot && canFly && localAuthority) {
            this.controlShip();
        } else if (localAuthority) {
            this.idleDrift();
        }

        this.tickTibannaConsumption();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.checkGroundStabilization();
    }

    /** Roda no server quando não há piloto: desacelera suavemente até hover parado */
    private void idleDrift() {
        forwardSpeed *= IDLE_DRAG;
        if (forwardSpeed < 0.005f) forwardSpeed = 0f;

        Vec3 forward = getForwardVector();
        Vec3 thrustMotion = forward.scale(forwardSpeed);

        Vec3 motion = this.getDeltaMovement();
        double newY;

        if (!hasTibannaFuel()) {
            // sem combustível: gravidade real, acelera a queda até a velocidade terminal
            newY = Math.max(motion.y - NO_FUEL_GRAVITY, -MAX_FALL_SPEED);
        } else {
            // com combustível mas sem piloto: hover neutro, suaviza de volta a 0
            newY = Mth.lerp(0.1, motion.y, 0.0);
        }

        this.setDeltaMovement(new Vec3(thrustMotion.x, newY, thrustMotion.z));

        if (!this.level().isClientSide) {
            this.entityData.set(FORWARD_SPEED, forwardSpeed);
        }
    }

    private void controlShip() {
        ShipType type = getShipType();
        if (type == null) return;
        if (!isFlightReady()) return; // nave incompleta não responde a input de voo

        if (rollLeft) addLocalRoll(-MAX_ROLL_SPEED);
        if (rollRight) addLocalRoll(MAX_ROLL_SPEED);

        Vec3 forward = getForwardVector();

        boolean idle = !thrustForward && !thrustBackward;

        if (thrustForward) {
            forwardSpeed = Math.min(forwardSpeed + type.acceleration(), type.maxSpeed());
        } else if (thrustBackward) {
            forwardSpeed = Math.max(forwardSpeed - BRAKE_DRAG_ACCEL, 0);
        } else {
            forwardSpeed *= PASSIVE_DRAG;
            if (forwardSpeed < 0.005f) forwardSpeed = 0f;
        }

        Vec3 thrustMotion = forward.scale(forwardSpeed);
        double verticalOverride = thrustMotion.y;

// dentro de controlShip(), no lugar do bloco ascend/descend atual:
        if (ascend) {
            currentVerticalSpeed = Math.min(currentVerticalSpeed + VERTICAL_ACCEL, VERTICAL_SPEED);
        } else if (descend) {
            currentVerticalSpeed = Math.max(currentVerticalSpeed - VERTICAL_ACCEL, -VERTICAL_SPEED);
        } else {
            // decai suavemente até zero em vez de zerar na hora
            if (currentVerticalSpeed > 0) {
                currentVerticalSpeed = Math.max(currentVerticalSpeed - VERTICAL_ACCEL, 0);
            } else if (currentVerticalSpeed < 0) {
                currentVerticalSpeed = Math.min(currentVerticalSpeed + VERTICAL_ACCEL, 0);
            }
        }

        if (ascend || descend || currentVerticalSpeed != 0f) {
            verticalOverride = currentVerticalSpeed;
        }

        if (ascend || descend) {
            verticalOverride = currentVerticalSpeed;
        }

        if (thrustForward || ascend) {
            gravityPull = Math.max(gravityPull - GRAVITY_RECOVERY, 0);
        } else if (thrustBackward) {
            gravityPull = Math.min(gravityPull + GRAVITY_BRAKE_ACCEL, MAX_GRAVITY_PULL);
        }

        if (!ascend && !descend) {
            if (gravityPull > 0) {
                verticalOverride -= gravityPull;
            } else if (idle && forwardSpeed < 0.01f) {
                verticalOverride -= IDLE_GRAVITY_PULL;
            }
        }

        Vec3 newMotion = new Vec3(thrustMotion.x, verticalOverride, thrustMotion.z);
        this.setDeltaMovement(newMotion);

        if (!this.level().isClientSide) {
            this.entityData.set(FORWARD_SPEED, forwardSpeed);
        }
    }

    private void checkGroundStabilization() {
        if (this.level().isClientSide) return;
        if (ascend || descend) return;

        LivingEntity controller = this.getControllingPassenger();
        if (controller instanceof Player p && p.getData(net.netherway.starwarschaincode.component.ModAttachments.HYPERSPACE_TRAVEL).traveling) {
            return; // não mexe na orientação durante hyperespaço
        }

        BlockHitResult hit = this.level().clip(new ClipContext(
                this.position(),
                this.position().subtract(0, GROUND_CHECK_DISTANCE, 0),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));

        boolean nearGround = hit.getType() != HitResult.Type.MISS;
        boolean nearlyStill = Math.abs(this.getDeltaMovement().y) < 0.1 && forwardSpeed < 0.05f;
        boolean noPilot = this.getControllingPassenger() == null;

        if (nearGround && (nearlyStill || noPilot)) {
            Vector3f currentEuler = orientation.getEulerAnglesYXZ(new Vector3f());
            float currentYawOnly = currentEuler.y;

            Quaternionf level = new Quaternionf().rotateY(currentYawOnly);

            // Sem piloto: nivela rápido e travado (evita ficar "preso no meio"); com piloto: gradual como antes
            float lerpSpeed = noPilot ? 0.3f : GROUND_STABILIZE_LERP;
            orientation.slerp(level, lerpSpeed);
            orientation.normalize();
            syncOrientation();
        }
    }

    private static final Vec3 SEAT_OFFSET = new Vec3(0, 0, 0); // ajusta na prática: y=altura do assento, z=quão pra trás/frente

    @Override
    protected void positionRider(Entity passenger, MoveFunction moverType) {
        if (passenger == this.getControllingPassenger()) {
            Vector3f rotatedOffset = new Vector3f((float) SEAT_OFFSET.x, (float) SEAT_OFFSET.y, (float) SEAT_OFFSET.z);
            orientation.transform(rotatedOffset);

            Vec3 seatPos = this.position().add(rotatedOffset.x, rotatedOffset.y, rotatedOffset.z);
            moverType.accept(passenger, seatPos.x, seatPos.y, seatPos.z);
            return;
        }

        super.positionRider(passenger, moverType);

        if (passenger instanceof LivingEntity living) {
            Vector3f euler = orientation.getEulerAnglesYXZ(new Vector3f());
            float yawDegrees = (float) Math.toDegrees(-euler.y);
            living.setYRot(yawDegrees);
            living.setYBodyRot(yawDegrees);
            living.setYHeadRot(yawDegrees);
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false; // nave nunca sofre dano de queda
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    // ================== Persistência ==================

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        ShipType type = getShipType();
        if (type != null) {
            tag.putString("ShipType", type.id().toString());
        }
        tag.putFloat("OrientX", orientation.x);
        tag.putFloat("OrientY", orientation.y);
        tag.putFloat("OrientZ", orientation.z);
        tag.putFloat("OrientW", orientation.w);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("ShipType")) {
            ShipType type = ModShipTypes.byId(ResourceLocation.parse(tag.getString("ShipType")));
            if (type != null) {
                this.setShipType(type);
            }
        }
        if (tag.contains("OrientW")) {
            orientation.set(
                    tag.getFloat("OrientX"),
                    tag.getFloat("OrientY"),
                    tag.getFloat("OrientZ"),
                    tag.getFloat("OrientW")
            );
            prevOrientation.set(orientation);
            syncOrientation();
        }
    }
}