package net.netherway.starwarschaincode.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.item.custom.*;

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
    public static final DeferredItem<Item> RAW_POLYMER_RESIN = ITEMS.registerSimpleItem("raw_polymer_resin");
    public static final DeferredItem<Item> NON_REFINED_PLASTOID_COMPOUND = ITEMS.registerSimpleItem("non_refined_plastoid_compound");
    public static final DeferredItem<Item> PLASTOID = ITEMS.registerSimpleItem("plastoid");

    public static final DeferredItem<Item> TATOOINE_MODEL = ITEMS.registerItem("tatooine_model",
            properties -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EARTH_MODEL = ITEMS.registerItem("earth_model",
            properties -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> IRON_ROD = ITEMS.registerSimpleItem("iron_rod");
    public static final DeferredItem<Item> BLUEPRINT_READER = ITEMS.registerSimpleItem("blueprint_reader");
    public static final DeferredItem<Item> WIRE = ITEMS.registerSimpleItem("wire");
    public static final DeferredItem<Item> SCREEN = ITEMS.registerSimpleItem("screen");
    public static final DeferredItem<Item> READER = ITEMS.registerSimpleItem("reader");
    public static final DeferredItem<Item> ALUMINUM_PLATE = ITEMS.registerSimpleItem("aluminum_plate");
    public static final DeferredItem<Item> DOONIUM_WING = ITEMS.registerSimpleItem("doonium_wing");
    public static final DeferredItem<Item> QUADANIUM_COATING = ITEMS.registerSimpleItem("quadanium_coating");
    public static final DeferredItem<Item> SOLAR_COLLECTOR_PANEL = ITEMS.registerSimpleItem("solar_collector_panel");
    public static final DeferredItem<Item> COPPER_PLATE = ITEMS.registerSimpleItem("copper_plate");
    public static final DeferredItem<Item> DOONIUM_PLATE = ITEMS.registerSimpleItem("doonium_plate");
    public static final DeferredItem<Item> IRON_PLATE = ITEMS.registerSimpleItem("iron_plate");
    public static final DeferredItem<Item> POLISHED_DOLOVITE_PLATE = ITEMS.registerSimpleItem("polished_dolovite_plate");
    public static final DeferredItem<Item> WHITE_CLOTH = ITEMS.registerSimpleItem("white_cloth");
    public static final DeferredItem<Item> BLACK_CLOTH = ITEMS.registerSimpleItem("black_cloth");
    public static final DeferredItem<Item> COTTON_FIBER = ITEMS.registerSimpleItem("cotton_fiber");

    public static final DeferredItem<Item> Z_95_HEADHUNTER_BLUEPRINT = ITEMS.registerItem("z_95_headhunter_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> HYPERDRIVE_BLUEPRINT = ITEMS.registerItem("hyperdrive_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> REPULSORLIFT_GENERATOR_BLUEPRINT = ITEMS.registerItem("repulsorlift_generator_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> NAVICOMPUTER_BLUEPRINT = ITEMS.registerItem("navicomputer_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> LIFE_SUPPORT_BLUEPRINT = ITEMS.registerItem("life_support_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> MAIN_REACTOR_BLUEPRINT = ITEMS.registerItem("main_reactor_blueprint",
            properties -> new Item(new Item.Properties().stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    public static final DeferredItem<Item> PORTABLE_SOLAR_COLLECTOR = ITEMS.registerItem("portable_solar_collector",
            properties -> new PortableSolarCollectorItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> TIBANNA_GAS_CAPSULE = ITEMS.registerItem("tibanna_gas_capsule",
            properties -> new TibannaGasCapsuleItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> PEN = ITEMS.registerItem("pen",
            properties -> new Item(properties), new Item.Properties().stacksTo(1).durability(250));
    public static final DeferredItem<Item> POWER_PACK = ITEMS.registerItem("power_pack",
            properties -> new PowerPackItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> ENERGY_CELL = ITEMS.registerItem("energy_cell",
            properties -> new EnergyCellItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ZOLTWEN_D3X_SCOPE_ATTACHMENT =
            ITEMS.register("zoltwen_d3x_scope_attachment",
                    () -> new ScopeAttachmentItem(new Item.Properties(), "zoltwen_d3x", 2.5f, .3f, .6f));

    public static final DeferredItem<Item> MEDIUM_BARREL_ATTACHMENT =
            ITEMS.register("medium_barrel_attachment",
                    () -> new BarrelAttachmentItem(new Item.Properties(), "medium", 1.5f));

    public static final DeferredItem<Item> WOODEN_STOCK_ATTACHMENT =
            ITEMS.register("wooden_stock_attachment",
                    () -> new StockAttachmentItem(new Item.Properties(), "wooden", 1.1f));

    public static final DeferredItem<Item> DL_44 = ITEMS.registerItem("dl_44",
            properties -> new WeaponItem(properties, 9.5f, 3f, 1, 20), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> LIGHTSABER = ITEMS.registerItem("lightsaber",
            properties -> new LightsaberItem(properties.attributes(LightsaberItem.createAttributes()), 12), new Item.Properties().stacksTo(1));


    public static final DeferredItem<Item> PLATFORM = ITEMS.registerItem("platform_controller",
            properties -> new PlatformItem(properties), new Item.Properties().stacksTo(1));


    public static final DeferredItem<Item> WISDOM_1_PART_1 = ITEMS.registerItem("wisdom_1_part_1",
            properties -> new HiltPartItem(properties, HiltPartType.PART_1), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> TRAGEDY_1_PART_1 = ITEMS.registerItem("tragedy_1_part_1",
            properties -> new HiltPartItem(properties, HiltPartType.PART_1), new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> WISDOM_1_PART_2 = ITEMS.registerItem("wisdom_1_part_2",
            properties -> new HiltPartItem(properties, HiltPartType.PART_2), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> TRAGEDY_1_PART_2 = ITEMS.registerItem("tragedy_1_part_2",
            properties -> new HiltPartItem(properties, HiltPartType.PART_2), new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> WISDOM_1_PART_3 = ITEMS.registerItem("wisdom_1_part_3",
            properties -> new HiltPartItem(properties, HiltPartType.PART_3), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> TRAGEDY_1_PART_3 = ITEMS.registerItem("tragedy_1_part_3",
            properties -> new HiltPartItem(properties, HiltPartType.PART_3), new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BLUE_KYBER_CRYSTAL = ITEMS.registerItem("blue_kyber_crystal",
            properties -> new KyberCrystalItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> GREEN_KYBER_CRYSTAL = ITEMS.registerItem("green_kyber_crystal",
            properties -> new KyberCrystalItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> RED_KYBER_CRYSTAL = ITEMS.registerItem("red_kyber_crystal",
            properties -> new KyberCrystalItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> PURPLE_KYBER_CRYSTAL = ITEMS.registerItem("purple_kyber_crystal",
            properties -> new KyberCrystalItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> CYAN_KYBER_CRYSTAL = ITEMS.registerItem("cyan_kyber_crystal",
            properties -> new KyberCrystalItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> ORANGE_KYBER_CRYSTAL = ITEMS.registerItem("orange_kyber_crystal",
            properties -> new KyberCrystalItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> YELLOW_KYBER_CRYSTAL = ITEMS.registerItem("yellow_kyber_crystal",
            properties -> new KyberCrystalItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> WHITE_KYBER_CRYSTAL = ITEMS.registerItem("white_kyber_crystal",
            properties -> new KyberCrystalItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> MAGENTA_KYBER_CRYSTAL = ITEMS.registerItem("magenta_kyber_crystal",
            properties -> new KyberCrystalItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
