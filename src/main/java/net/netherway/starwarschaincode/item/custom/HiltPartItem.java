package net.netherway.starwarschaincode.item.custom;

import net.minecraft.world.item.Item;
import net.netherway.starwarschaincode.item.HiltPartType;

public class HiltPartItem extends Item {

    private final HiltPartType type;

    public HiltPartItem(Properties properties, HiltPartType type) {
        super(properties);
        this.type = type;
    }

    public HiltPartType getType() {
        return type;
    }
}