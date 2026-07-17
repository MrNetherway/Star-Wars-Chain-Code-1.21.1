package net.netherway.starwarschaincode.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShipInventoryData {
    public static final int MAX_SLOTS = 8;

    public final NonNullList<ItemStack> items;

    public ShipInventoryData() {
        this.items = NonNullList.withSize(MAX_SLOTS, ItemStack.EMPTY);
    }

    private ShipInventoryData(List<ItemStack> loaded) {
        this.items = NonNullList.withSize(MAX_SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < Math.min(loaded.size(), MAX_SLOTS); i++) {
            items.set(i, loaded.get(i));
        }
    }

    public static final Codec<ShipInventoryData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items").forGetter(data -> data.items)
    ).apply(instance, ShipInventoryData::new));

    public ItemStack get(int slot) {
        return slot >= 0 && slot < MAX_SLOTS ? items.get(slot) : ItemStack.EMPTY;
    }

    public void set(int slot, ItemStack stack) {
        if (slot >= 0 && slot < MAX_SLOTS) {
            items.set(slot, stack);
        }
    }
}