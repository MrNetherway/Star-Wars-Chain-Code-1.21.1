package net.netherway.starwarschaincode.item.custom;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WeaponGeoModel extends GeoModel<WeaponItem> {

    @Override
    public ResourceLocation getModelResource(WeaponItem animatable) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(animatable);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(),
                "geo/item/" + id.getPath() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WeaponItem animatable) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(animatable);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(),
                "textures/item/" + id.getPath() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(WeaponItem animatable) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(animatable);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(),
                "animations/item/" + id.getPath() + ".animation.json");
    }
}