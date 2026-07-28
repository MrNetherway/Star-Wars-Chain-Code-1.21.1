package net.netherway.starwarschaincode.component;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public record WeaponAttachmentData(ResourceLocation itemId) {

    public static final Codec<WeaponAttachmentData> CODEC =
            ResourceLocation.CODEC.xmap(
                    WeaponAttachmentData::new,
                    WeaponAttachmentData::itemId
            );
}