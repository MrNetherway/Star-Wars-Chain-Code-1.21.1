package net.netherway.starwarschaincode.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.custom.BlasterBoltEntity;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, StarWarsChainCode.MOD_ID);

    public static final Supplier<EntityType<BlasterBoltEntity>> BLASTER_BOLT =
            ENTITY_TYPES.register("blasterboltentity", () -> EntityType.Builder.<BlasterBoltEntity>of(
                    BlasterBoltEntity::new,
                    MobCategory.MISC
            ).sized(0.2f, 0.2f).build("blasterboltentity"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
