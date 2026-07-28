package net.netherway.starwarschaincode.block;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.netherway.starwarschaincode.StarWarsChainCode;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class ModBlockTags {
    public static final TagKey<Block> ASTEROID_BLOCKS =
            TagKey.create(net.minecraft.core.registries.Registries.BLOCK, fromNamespaceAndPath(StarWarsChainCode.MOD_ID, "asteroid_blocks"));
}