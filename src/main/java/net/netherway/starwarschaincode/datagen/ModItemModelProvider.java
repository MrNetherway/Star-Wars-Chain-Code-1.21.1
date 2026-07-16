package net.netherway.starwarschaincode.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, StarWarsChainCode.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.RAW_DOONIUM.get());
        basicItem(ModItems.BASE_DOONIUM.get());
        basicItem(ModItems.DOONIUM_BAR.get());
        basicItem(ModItems.RAW_BAUXITE.get());
        basicItem(ModItems.ALUMINA.get());
        basicItem(ModItems.ALUMINUM_BAR.get());
        basicItem(ModItems.RAW_QUADANIUM.get());
        basicItem(ModItems.BASE_QUADANIUM.get());
        basicItem(ModItems.QUADANIUM_BAR.get());
        basicItem(ModItems.RAW_DOLOVITE.get());
        basicItem(ModItems.POLISHED_DOLOVITE.get());

        basicItem(ModItems.IRON_ROD.get());
        basicItem(ModItems.BLUEPRINT_READER.get());
        basicItem(ModItems.WIRE.get());
        basicItem(ModItems.SCREEN.get());
        basicItem(ModItems.READER.get());
        basicItem(ModItems.PEN.get());
        basicItem(ModItems.ALUMINUM_PLATE.get());
        basicItem(ModItems.DOONIUM_WING.get());
        basicItem(ModItems.ENERGY_CELL.get());
        basicItem(ModItems.SOLAR_CELL.get());
        basicItem(ModItems.QUADANIUM_COATING.get());
        basicItem(ModItems.SOLAR_COLLECTOR_PANEL.get());
        basicItem(ModItems.COPPER_PLATE.get());
        basicItem(ModItems.DOONIUM_PLATE.get());
        basicItem(ModItems.IRON_PLATE.get());
        basicItem(ModItems.POLISHED_DOLOVITE_PLATE.get());
        basicItem(ModItems.TIBANNA_GAS_CAPSULE.get());

        basicItem(ModItems.Z_95_HEADHUNTER_BLUEPRINT.get());
        basicItem(ModItems.HYPERDRIVE_BLUEPRINT.get());
        basicItem(ModItems.REPULSORLIFT_GENERATOR_BLUEPRINT.get());
        basicItem(ModItems.NAVICOMPUTER_BLUEPRINT.get());
        basicItem(ModItems.LIFE_SUPPORT_BLUEPRINT.get());
        basicItem(ModItems.MAIN_REACTOR_BLUEPRINT.get());

        basicItem(ModItems.PORTABLE_SOLAR_COLLECTOR.get());
    }
}
