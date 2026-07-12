package net.netherway.starwarschaincode.worldgen.asteroid;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;

public class AsteroidGenerator {


    public static void generate(WorldGenLevel level, BlockPos pos, int radius) {

        RandomSource random = RandomSource.create();


        for(int x = -radius; x <= radius; x++){

            for(int y = -radius; y <= radius; y++){

                for(int z = -radius; z <= radius; z++){


                    double distance =
                            Math.sqrt(
                                    x*x +
                                            y*y +
                                            z*z
                            );


                    // deixa irregular
                    double noise = random.nextDouble() * 2 - 1;


                    if(distance <= radius + noise){

                        level.setBlock(
                                pos.offset(x,y,z),
                                Blocks.BLACKSTONE.defaultBlockState(),
                                3
                        );

                    }

                }

            }

        }

    }
}