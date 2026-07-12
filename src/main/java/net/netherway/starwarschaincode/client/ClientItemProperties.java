package net.netherway.starwarschaincode.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.item.ModItems;
import net.netherway.starwarschaincode.component.ModDataComponents;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class ClientItemProperties {

    @SubscribeEvent
    public static void register(RegisterClientReloadListenersEvent event) {

        ItemProperties.register(
                ModItems.LIGHTSABER.get(),
                ResourceLocation.fromNamespaceAndPath(
                        StarWarsChainCode.MOD_ID,
                        "activated"
                ),
                (stack, level, entity, seed) -> {

                    return stack.getOrDefault(
                            ModDataComponents.ACTIVATED.get(),
                            false
                    ) ? 1.0F : 0.0F;

                }


        );
    }
}