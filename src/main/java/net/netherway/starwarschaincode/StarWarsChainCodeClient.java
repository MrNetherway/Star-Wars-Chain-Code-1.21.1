package net.netherway.starwarschaincode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.client.WeaponAimModifier;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.entity.ModEntities;
import net.netherway.starwarschaincode.entity.client.BlasterBoltRenderer;
import net.netherway.starwarschaincode.item.ModItems;
import net.netherway.starwarschaincode.item.client.LightsaberItemRenderer;
import net.netherway.starwarschaincode.item.client.WeaponItemRenderer;
import net.netherway.starwarschaincode.item.custom.WeaponItem;

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

    public static final ResourceLocation ANIMATION_LAYER_ID_RIGHT =
            ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "weapon_layer_right");
    public static final ResourceLocation ANIMATION_LAYER_ID_LEFT =
            ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "weapon_layer_left");

    private static void registerWeaponArmLayer(boolean isLeft) {
        ResourceLocation layerId = isLeft ? ANIMATION_LAYER_ID_LEFT : ANIMATION_LAYER_ID_RIGHT;

        com.zigythebird.playeranim.api.PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                layerId,
                1500,
                player -> {
                    com.zigythebird.playeranim.animation.PlayerAnimationController controller =
                            new com.zigythebird.playeranim.animation.PlayerAnimationController(player,
                                    (c, state, animSetter) -> {
                                        boolean mainIsRight = player.getMainArm() == net.minecraft.world.entity.HumanoidArm.RIGHT;

                                        net.minecraft.world.item.ItemStack relevantStack = isLeft
                                                ? (mainIsRight ? player.getOffhandItem() : player.getMainHandItem())
                                                : (mainIsRight ? player.getMainHandItem() : player.getOffhandItem());

                                        if (relevantStack.getItem() instanceof WeaponItem) {
                                            return animSetter.setAnimation(
                                                    com.zigythebird.playeranim.animation.PlayerRawAnimationBuilder.begin()
                                                            .thenLoop(ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "hold_idle"))
                                                            .build()
                                            );
                                        }

                                        return com.zigythebird.playeranimcore.enums.PlayState.STOP;
                                    }
                            );

                    controller.addModifierLast(new WeaponAimModifier(player, isLeft));

                    if (isLeft) {
                        com.zigythebird.playeranimcore.animation.layered.modifier.MirrorModifier mirror =
                                new com.zigythebird.playeranimcore.animation.layered.modifier.MirrorModifier();
                        mirror.enabled = true; // sempre espelhado nesse controller, já que ele É o layer esquerdo
                        controller.addModifierLast(mirror);
                    }

                    return controller;
                }
        );
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

            registerWeaponArmLayer(false); // braço direito
            registerWeaponArmLayer(true);  // braço esquerdo
        });
    }

    @SubscribeEvent
    static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private final WeaponItemRenderer renderer = new WeaponItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, ModItems.DL_44.get());

        event.registerItem(new IClientItemExtensions() {
            private final LightsaberItemRenderer renderer = new LightsaberItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, ModItems.LIGHTSABER.get());
    }
}
