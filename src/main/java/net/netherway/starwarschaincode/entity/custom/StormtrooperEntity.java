package net.netherway.starwarschaincode.entity.custom;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.netherway.starwarschaincode.entity.ai.FactionHurtByTargetGoal;
import net.netherway.starwarschaincode.entity.ai.FollowCommanderGoal;
import net.netherway.starwarschaincode.entity.ai.RangedBlasterAttackGoal;
import net.netherway.starwarschaincode.entity.custom.BlasterBoltEntity;
import net.netherway.starwarschaincode.entity.ai.FactionHostileTargetGoal;
import net.netherway.starwarschaincode.faction.Faction;
import net.netherway.starwarschaincode.faction.FactionMember;
import net.netherway.starwarschaincode.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StormtrooperEntity extends Monster implements RangedAttackMob, FactionMember {


    public StormtrooperEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new MoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnData) {

        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData);

        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.DL_44.get()));

        return data;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);

        if (!this.level().isClientSide && result && source.getEntity() instanceof LivingEntity attacker) {
            boolean sameFaction = attacker instanceof FactionMember member && member.getFaction() == this.getFaction();
            if (!sameFaction) {
                this.alertNearbyTroopers(attacker);
            }
        }

        return result;
    }

    private void alertNearbyTroopers(LivingEntity attacker) {
        List<Mob> nearby = this.level().getEntitiesOfClass(
                Mob.class,
                this.getBoundingBox().inflate(16.0D),
                mob -> mob instanceof FactionMember member && member.getFaction() == this.getFaction()
        );

        for (Mob mob : nearby) {
            if (mob != this && mob.getTarget() == null) {
                mob.setTarget(attacker);
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedBlasterAttackGoal<>(this, 1.0D, 20, 15.0F));
        this.targetSelector.addGoal(1, new FactionHurtByTargetGoal(this, this::getFaction));
        this.goalSelector.addGoal(2, new FollowCommanderGoal(this, 1.0D, 5.0F, 32.0F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));


        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new FactionHostileTargetGoal<>(this, Player.class, true, this::getFaction));
    }

    @Override
    protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.DL_44.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!(this.getMainHandItem().getItem() instanceof net.netherway.starwarschaincode.item.custom.WeaponItem weapon)) {
            return;
        }

        BlasterBoltEntity bolt = new BlasterBoltEntity(this.level(), this);
        bolt.setDamage(weapon.getDamage());

        double dx = target.getX() - this.getX();
        double dy = target.getY(0.5D) - bolt.getY();
        double dz = target.getZ() - this.getZ();

        bolt.shootFromRotation(this, dx, dy, dz, weapon.getProjectileSpeed(), 6.0F);
        this.level().addFreshEntity(bolt);

        this.playSound(net.minecraft.sounds.SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 1.0F);
    }

    @Override
    public void setAggressive(boolean aggressive) {
        super.setAggressive(aggressive);
    }

    @Override
    public Faction getFaction() {
        return Faction.EMPIRE;
    }
}