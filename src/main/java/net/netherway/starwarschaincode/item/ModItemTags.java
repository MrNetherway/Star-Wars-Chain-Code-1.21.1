package net.netherway.starwarschaincode.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.netherway.starwarschaincode.StarWarsChainCode;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class ModItemTags {
    public static final TagKey<Item> HYPERDRIVE_COMPONENT =
            TagKey.create(net.minecraft.core.registries.Registries.ITEM, fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "component_hyperdrive"));
    public static final TagKey<Item> LIFE_SUPPORT_COMPONENT =
            TagKey.create(net.minecraft.core.registries.Registries.ITEM, fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "component_life_support"));
    public static final TagKey<Item> REPULSORLIFT_GENERATOR_COMPONENT =
            TagKey.create(net.minecraft.core.registries.Registries.ITEM, fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "component_repulsorlift_generator"));
    public static final TagKey<Item> NAVICOMPUTER_COMPONENT =
            TagKey.create(net.minecraft.core.registries.Registries.ITEM, fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "component_navicomputer"));
    public static final TagKey<Item> MAIN_REACTOR_COMPONENT =
            TagKey.create(net.minecraft.core.registries.Registries.ITEM, fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "component_main_reactor"));
}