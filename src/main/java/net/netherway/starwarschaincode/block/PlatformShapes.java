package net.netherway.starwarschaincode.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlatformShapes {
    public static final VoxelShape PLATFORM_SHAPE = Block.box(0, 0, 0, 16, 8, 16); // 2px de altura
}