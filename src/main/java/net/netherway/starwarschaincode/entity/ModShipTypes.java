package net.netherway.starwarschaincode.entity;

import net.minecraft.resources.ResourceLocation;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.ShipType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModShipTypes {
    private static final Map<ResourceLocation, ShipType> REGISTRY = new LinkedHashMap<>();

    public static final ShipType Z95_HEADHUNTER = register(new ShipType(
            id("z95headhunter"),
            3f,   // maxSpeed
            0.05f,  // acceleration
            0.92f,  // friction
            3.0f,   // turnSpeed
            15f, 2f, // hitbox
            14f,
                    .05f,
            id("textures/entity/z95headhunter.png"),
            List.of(
                    new ShipType.ComponentSlot("hyperdrive", ShipType.ComponentType.HYPERDRIVE, true),
                    new ShipType.ComponentSlot("life_support", ShipType.ComponentType.LIFE_SUPPORT, true),
                    new ShipType.ComponentSlot("repulsorlift_generator", ShipType.ComponentType.REPULSORLIFT_GENERATOR, true),
                    new ShipType.ComponentSlot("navicomputer", ShipType.ComponentType.NAVICOMPUTER, true),
                    new ShipType.ComponentSlot("main_reactor", ShipType.ComponentType.MAIN_REACTOR, true),
                    new ShipType.ComponentSlot("energy", ShipType.ComponentType.ENERGY, true)
            )
    ));

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, path);
    }

    private static ShipType register(ShipType type) {
        REGISTRY.put(type.id(), type);
        return type;
    }

    public static ShipType byId(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static Map<ResourceLocation, ShipType> all() {
        return REGISTRY;
    }
}