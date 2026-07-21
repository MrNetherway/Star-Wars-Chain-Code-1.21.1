package net.netherway.starwarschaincode.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.ModEntities;
import net.netherway.starwarschaincode.entity.client.*;
import net.netherway.starwarschaincode.entity.client.ships.Z95HeadhunterModel;
import net.netherway.starwarschaincode.entity.custom.StormtrooperCommanderEntity;
import net.netherway.starwarschaincode.entity.custom.StormtrooperEntity;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BlasterBoltModel.LAYER_LOCATION, BlasterBoltModel::createBodyLayer);
        event.registerLayerDefinition(Z95HeadhunterModel.LAYER_LOCATION, Z95HeadhunterModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHIP.get(), ShipRenderer::new);
        event.registerEntityRenderer(ModEntities.BLASTER_BOLT.get(), BlasterBoltRenderer::new);
        event.registerEntityRenderer(ModEntities.STORMTROOPER.get(), StormtrooperRenderer::new);
        event.registerEntityRenderer(ModEntities.STORMTROOPER_COMMANDER.get(), StormtrooperCommanderRenderer::new);

    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(
                ModEntities.STORMTROOPER.get(),
                StormtrooperEntity.createAttributes().build()
        );
        event.put(
                ModEntities.STORMTROOPER_COMMANDER.get(),
                StormtrooperCommanderEntity.createAttributes().build()
        );
    }

}
