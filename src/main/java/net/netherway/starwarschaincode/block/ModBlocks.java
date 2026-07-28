package net.netherway.starwarschaincode.block;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.block.custom.*;
import net.netherway.starwarschaincode.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(StarWarsChainCode.MOD_ID);

    public static final DeferredBlock<Block> LAVA_REFINER = registerBlock("lava_refiner",
            () -> new LavaRefinerBlock(BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(
                    LavaRefinerBlock.HAS_LAVA) ? 6 : 0
            ).strength(5f)));
    public static final DeferredBlock<Block> CHARGED_CHAMBER = registerBlock("charged_chamber",
            () -> new ChargedChamberBlock(BlockBehaviour.Properties.of().strength(5f)));
    public static final DeferredBlock<Block> BLUEPRINT_BUILDER = registerBlock("blueprint_builder",
            () -> new BlueprintBuilderBlock(BlockBehaviour.Properties.of().strength(3.5f)));
    public static final DeferredBlock<Block> WEAPON_WORKBENCH = registerBlock("weapon_workbench",
            () -> new WeaponWorkbenchBlock(BlockBehaviour.Properties.of().strength(3.5f)));
    public static final DeferredBlock<Block> LIGHTSABER_ASSEMBLER = registerBlock("lightsaber_assembler",
            () -> new LightsaberAssemblerBlock(BlockBehaviour.Properties.of().strength(3.5f)));
    public static final DeferredBlock<PlatformControllerBlock> PLATFORM_CONTROLLER = registerBlockWithoutItem("platform_controller",
            () -> new PlatformControllerBlock(BlockBehaviour.Properties.of().strength(3.5f).noOcclusion()));
    public static final DeferredBlock<PlatformFillerBlock> PLATFORM_FILLER = registerBlockWithoutItem("platform_filler",
            () -> new PlatformFillerBlock(BlockBehaviour.Properties.of().strength(3.5f).noOcclusion()));
    public static final DeferredBlock<Block> TIBANNA_GAS = registerBlock("tibanna_gas",
            () -> new TibannaGasBlock(BlockBehaviour.Properties.of().noLootTable().noOcclusion()
                    .isViewBlocking((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)));

    public static final DeferredBlock<Block> ALUMINUM_BLOCK = registerBlock("aluminum_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(5f)));

    public static final DeferredBlock<Block> DOONIUM_ORE = registerBlock("doonium_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_ORE = registerBlock("bauxite_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> QUADANIUM_ORE = registerBlock("quadanium_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> DOLOVITE_ORE = registerBlock("dolovite_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_DEEPSLATE_ORE = registerBlock("bauxite_deepslate_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 6),
                    BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithoutItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
