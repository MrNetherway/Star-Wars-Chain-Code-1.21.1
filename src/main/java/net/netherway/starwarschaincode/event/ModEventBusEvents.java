package net.netherway.starwarschaincode.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.client.BlasterBoltModel;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BlasterBoltModel.LAYER_LOCATION, BlasterBoltModel::createBodyLayer);
    }

}
