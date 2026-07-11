package net.netherway.starwarschaincode.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.item.custom.WeaponItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(StarWarsChainCode.MOD_ID);

    public static final DeferredItem<Item> RAW_DOONIUM = ITEMS.registerSimpleItem("raw_doonium");

    public static final DeferredItem<Item> DL_44 = ITEMS.registerItem("dl_44",
            properties -> new WeaponItem(properties, 12, 2.5f, 1));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
