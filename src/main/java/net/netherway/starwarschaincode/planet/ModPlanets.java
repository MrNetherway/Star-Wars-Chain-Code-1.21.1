package net.netherway.starwarschaincode.planet;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.item.ModItems;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModPlanets {

    private static final Map<ResourceLocation, PlanetData> PLANETS = new LinkedHashMap<>();

    public static final PlanetData TATOOINE = register(new PlanetData(
            rl("tatooine"),
            ResourceKey.create(Registries.DIMENSION, rl("tatooine")), // ver nota abaixo sobre import
            0.5, 100, 0.5,
            rl("textures/gui/planets/tatooine_icon.png"),
            120, 80,
            "planet.starwarschaincode.tatooine",
            new ItemStack(ModItems.TATOOINE_MODEL.get())
    ));

    public static final PlanetData EARTH = register(new PlanetData(
            rl("overworld"),
            Level.OVERWORLD, // ver nota abaixo sobre import
            0.5, 100, 0.5,
            rl("textures/gui/planets/earth_icon.png"),
            60, 80,
            "planet.starwarschaincode.overworld",
            new ItemStack(ModItems.EARTH_MODEL.get())
    ));

    // adiciona mais planetas aqui conforme for criando as dimensões

    private static PlanetData register(PlanetData data) {
        PLANETS.put(data.id(), data);
        return data;
    }

    public static PlanetData byId(ResourceLocation id) {
        return PLANETS.get(id);
    }

    public static Map<ResourceLocation, PlanetData> all() {
        return PLANETS;
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, path);
    }
}