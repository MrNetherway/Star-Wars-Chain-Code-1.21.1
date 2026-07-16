package net.netherway.starwarschaincode.worldgen.asteroid;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.netherway.starwarschaincode.block.ModBlocks;

public class AsteroidGenerator {

    private static final Block[] ORES = {
            ModBlocks.DOONIUM_ORE.get(),
            ModBlocks.DOLOVITE_ORE.get(),
            ModBlocks.QUADANIUM_ORE.get(),
            ModBlocks.DOLOVITE_ORE.get()
    };


    public static void generate(WorldGenLevel level, BlockPos pos, int radius, RandomSource random) {

        int craterRadius = radius + random.nextIntBetweenInclusive(3, 6);

        for (int x = -craterRadius; x <= craterRadius; x++) {
            for (int y = -craterRadius; y <= 0; y++) {
                for (int z = -craterRadius; z <= craterRadius; z++) {

                    double dist = Math.sqrt(
                            x*x +
                                    (y*1.6)*(y*1.6) +
                                    z*z
                    );

                    if (dist <= craterRadius) {

                        level.setBlock(
                                pos.offset(x, y, z),
                                Blocks.AIR.defaultBlockState(),
                                3
                        );
                    }
                }
            }
        }

        for (int x = -radius; x <= radius; x++) {

            for (int y = -radius; y <= radius; y++) {

                for (int z = -radius; z <= radius; z++) {


                    double distance =
                            Math.sqrt(
                                    x * x +
                                            y * y +
                                            z * z
                            );


                    // deixa irregular
                    double noise = random.nextDouble() * 2 - 1;


                    if (distance <= radius + noise) {

                        level.setBlock(
                                pos.offset(x, y-3, z),
                                Blocks.BLACKSTONE.defaultBlockState(),
                                3
                        );

                    }

                }

            }

        }

        int veins = random.nextIntBetweenInclusive(2, 6);

        for (int i = 0; i < veins; i++) {
            generateOreVein(level, pos, radius, random);
        }



        for(int i=0;i<radius*3;i++){

            BlockPos firePos = pos.offset(
                    random.nextInt(radius*2)-radius,
                    0,
                    random.nextInt(radius*2)-radius
            );

            if(level.isEmptyBlock(firePos.above())
                    && level.getBlockState(firePos).isSolid()){

                level.setBlock(
                        firePos.above(),
                        Blocks.FIRE.defaultBlockState(),
                        3
                );
            }
        }

    }

    private static void generateOreVein(LevelAccessor level,
                                        BlockPos center,
                                        int radius,
                                        RandomSource random) {

        BlockPos start;
        int safeRadius = Math.max(2, radius - random.nextIntBetweenInclusive(1, 3));

        do {
            int x = center.getX() + random.nextInt(safeRadius * 2) - safeRadius;
            int y = center.getY() + random.nextInt(safeRadius * 2) - safeRadius;
            int z = center.getZ() + random.nextInt(safeRadius * 2) - safeRadius;

            start = new BlockPos(x, y, z);

        } while (!level.getBlockState(start).is(Blocks.BLACKSTONE));

        Block ore = ORES[random.nextInt(ORES.length)];
        int size = random.nextIntBetweenInclusive(4, 12);

        for (int i = 0; i < size; i++) {

            BlockPos pos = start.offset(
                    random.nextInt(5) - 2,
                    random.nextInt(5) - 2,
                    random.nextInt(5) - 2
            );

            if (level.getBlockState(pos).is(Blocks.BLACKSTONE)) {
                level.setBlock(pos, ore.defaultBlockState(), 3);
            }
        }
    }
}