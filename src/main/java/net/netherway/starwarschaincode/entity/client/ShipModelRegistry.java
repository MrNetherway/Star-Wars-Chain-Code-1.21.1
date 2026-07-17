package net.netherway.starwarschaincode.entity.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.entity.custom.ShipEntity;
import net.netherway.starwarschaincode.entity.ModShipTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ShipModelRegistry {
    private static final Map<ResourceLocation, Function<EntityModelSet, EntityModel<ShipEntity>>> FACTORIES = new HashMap<>();
    private static final Map<ResourceLocation, EntityModel<ShipEntity>> CACHE = new HashMap<>();

    static {
        FACTORIES.put(ModShipTypes.Z95_HEADHUNTER.id(),
                models -> new Z95HeadhunterModel(models.bakeLayer(Z95HeadhunterModel.LAYER_LOCATION)));

        // próxima nave: FACTORIES.put(ModShipTypes.N1_STARFIGHTER.id(), models -> new N1StarfighterModel(...));
    }

    public static void init(EntityModelSet modelSet) {
        CACHE.clear();
        FACTORIES.forEach((id, factory) -> CACHE.put(id, factory.apply(modelSet)));
    }

    public static EntityModel<ShipEntity> get(ResourceLocation shipTypeId) {
        return CACHE.get(shipTypeId);
    }
}