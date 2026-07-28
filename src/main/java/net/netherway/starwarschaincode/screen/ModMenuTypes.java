package net.netherway.starwarschaincode.screen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.screen.custom.*;

import java.util.UUID;
import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, StarWarsChainCode.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<LavaRefinerMenu>> LAVA_REFINER_MENU =
            registerMenuType("lava_refiner_menu", LavaRefinerMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ChargedChamberMenu>> CHARGED_CHAMBER_MENU =
            registerMenuType("charged_chamer_menu", ChargedChamberMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<BlueprintBuilderMenu>> BLUEPRINT_BUILDER_MENU =
            registerMenuType("blueprint_builder_menu", BlueprintBuilderMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<WeaponWorkbenchMenu>> WEAPON_WORKBENCH_MENU =
            registerMenuType("weapon_workbench_menu", WeaponWorkbenchMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<LightsaberAssemblerMenu>> LIGHTSABER_ASSEMBLER_MENU =
            registerMenuType("lightsaber_assembler_menu", LightsaberAssemblerMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<PlatformMenu>> PLATFORM_MENU =
            registerMenuType("platform_menu", (windowId, inv, buf) -> new PlatformMenu(windowId, inv, buf.readBlockPos()));

    public static final DeferredHolder<MenuType<?>, MenuType<ShipComponentMenu>> SHIP_COMPONENT_MENU =
            registerMenuType("ship_component_menu",
                    (windowId, inv, buf) -> new ShipComponentMenu(windowId, inv, buf.readVarInt()));

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                              IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
