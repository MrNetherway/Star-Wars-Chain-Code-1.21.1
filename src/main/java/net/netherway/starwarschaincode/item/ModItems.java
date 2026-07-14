package net.netherway.starwarschaincode.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.item.custom.PortableSolarCollectorItem;
import net.netherway.starwarschaincode.item.custom.WeaponItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(StarWarsChainCode.MOD_ID);

    public static final DeferredItem<Item> RAW_DOONIUM = ITEMS.registerSimpleItem("raw_doonium");
    public static final DeferredItem<Item> BASE_DOONIUM = ITEMS.registerSimpleItem("base_doonium");
    public static final DeferredItem<Item> DOONIUM_BAR = ITEMS.registerSimpleItem("doonium_bar");
    public static final DeferredItem<Item> RAW_BAUXITE = ITEMS.registerSimpleItem("raw_bauxite");
    public static final DeferredItem<Item> ALUMINA = ITEMS.registerSimpleItem("alumina");
    public static final DeferredItem<Item> ALUMINUM_BAR = ITEMS.registerSimpleItem("aluminum_bar");
    public static final DeferredItem<Item> RAW_QUADANIUM = ITEMS.registerSimpleItem("raw_quadanium");
    public static final DeferredItem<Item> BASE_QUADANIUM = ITEMS.registerSimpleItem("base_quadanium");
    public static final DeferredItem<Item> QUADANIUM_BAR = ITEMS.registerSimpleItem("quadanium_bar");
    public static final DeferredItem<Item> RAW_DOLOVITE = ITEMS.registerSimpleItem("raw_dolovite");
    public static final DeferredItem<Item> POLISHED_DOLOVITE = ITEMS.registerSimpleItem("polished_dolovite");

    public static final DeferredItem<Item> IRON_ROD = ITEMS.registerSimpleItem("iron_rod");
    public static final DeferredItem<Item> BLUEPRINT_READER = ITEMS.registerSimpleItem("blueprint_reader");
    public static final DeferredItem<Item> WIRE = ITEMS.registerSimpleItem("wire");
    public static final DeferredItem<Item> SCREEN = ITEMS.registerSimpleItem("screen");
    public static final DeferredItem<Item> READER = ITEMS.registerSimpleItem("reader");
    public static final DeferredItem<Item> PEN = ITEMS.registerSimpleItem("pen");
    public static final DeferredItem<Item> ALUMINUM_PLATE = ITEMS.registerSimpleItem("aluminum_plate");
    public static final DeferredItem<Item> DOONIUM_WING = ITEMS.registerSimpleItem("doonium_wing");
    public static final DeferredItem<Item> ENERGY_CELL = ITEMS.registerSimpleItem("energy_cell");
    public static final DeferredItem<Item> SOLAR_CELL = ITEMS.registerSimpleItem("solar_cell");
    public static final DeferredItem<Item> QUADANIUM_COATING = ITEMS.registerSimpleItem("quadanium_coating");
    public static final DeferredItem<Item> SOLAR_COLLECTOR_PANEL = ITEMS.registerSimpleItem("solar_collector_panel");

    public static final DeferredItem<Item> Z_95_HEADHUNTER_BLUEPRINT = ITEMS.registerSimpleItem("z_95_headhunter_blueprint");
    public static final DeferredItem<Item> HYPERDRIVE_BLUEPRINT = ITEMS.registerSimpleItem("hyperdrive_blueprint");
    public static final DeferredItem<Item> REPULSORLIFT_GENERATOR_BLUEPRINT = ITEMS.registerSimpleItem("repulsorlift_generator_blueprint");
    public static final DeferredItem<Item> NAVICOMPUTER_BLUEPRINT = ITEMS.registerSimpleItem("navicomputer_blueprint");
    public static final DeferredItem<Item> LIFE_SUPPORT_BLUEPRINT = ITEMS.registerSimpleItem("life_support_blueprint");
    public static final DeferredItem<Item> MAIN_REACTOR_BLUEPRINT = ITEMS.registerSimpleItem("main_reactor_blueprint");

    public static final DeferredItem<Item> PORTABLE_SOLAR_COLLECTOR = ITEMS.registerItem("portable_solar_collector",
            properties -> new PortableSolarCollectorItem(new Item.Properties()));


    public static final DeferredItem<Item> DL_44 = ITEMS.registerItem("dl_44",
            properties -> new WeaponItem(properties, 12, 2.5f, 1), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> LIGHTSABER = ITEMS.registerItem("lightsaber",
            properties -> new LightsaberItem(properties.attributes(LightsaberItem.createAttributes()), 12), new Item.Properties().stacksTo(1));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
