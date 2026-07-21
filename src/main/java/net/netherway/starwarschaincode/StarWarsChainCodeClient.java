package net.netherway.starwarschaincode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.entity.ModEntities;
import net.netherway.starwarschaincode.entity.client.BlasterBoltRenderer;
import net.netherway.starwarschaincode.item.ModItems;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = StarWarsChainCode.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class StarWarsChainCodeClient {
    public StarWarsChainCodeClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }


    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        StarWarsChainCode.LOGGER.info("HELLO FROM CLIENT SETUP");
        StarWarsChainCode.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        EntityRenderers.register(ModEntities.BLASTER_BOLT.get(), BlasterBoltRenderer::new);

        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.TIBANNA_GAS.get(), RenderType.translucent());

            ItemProperties.register(ModItems.TIBANNA_GAS_CAPSULE.get(),
                    ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "tibanna_filled"),
                    (stack, level, entity, seed) ->
                            stack.getOrDefault(ModDataComponents.TIBANNA_AMOUNT, 0) > 0 ? 1.0f : 0.0f);
        });
    }
}
