package net.netherway.starwarschaincode.item;

import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(StarWarsChainCode.MOD_ID);

    public static final DeferredItem<Item> RAW_DOONIUM = ITEMS.register("raw_doonium",
            () -> new Item(new Item.Properties()));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
