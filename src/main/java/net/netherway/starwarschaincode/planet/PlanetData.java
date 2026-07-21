package net.netherway.starwarschaincode.planet;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record PlanetData(
        ResourceLocation id,
        ResourceKey<Level> dimension,
        double spawnX, double spawnY, double spawnZ,
        ResourceLocation icon,     // textura SEPARADA do background
        int mapX, int mapY,         // posição do ícone no mapa (coordenada "de mundo" do mapa, não de tela)
        String displayName,
        ItemStack modelItem
) {}