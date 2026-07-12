package net.netherway.starwarschaincode.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.netherway.starwarschaincode.StarWarsChainCode;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, StarWarsChainCode.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
