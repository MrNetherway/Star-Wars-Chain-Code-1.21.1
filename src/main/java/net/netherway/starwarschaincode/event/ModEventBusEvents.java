package net.netherway.starwarschaincode.event;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.ModEntities;
import net.netherway.starwarschaincode.entity.client.*;

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
    }

}
