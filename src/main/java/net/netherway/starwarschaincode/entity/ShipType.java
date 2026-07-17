package net.netherway.starwarschaincode.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.netherway.starwarschaincode.item.ModItemTags;

import java.util.List;

public record ShipType(
        ResourceLocation id,
        float maxSpeed,
        float acceleration,
        float friction,
        float turnSpeed,
        float hitboxWidth,
        float hitboxHeight,
        ResourceLocation texture,
        List<ComponentSlot> componentSlots
) {
    public record ComponentSlot(String slotId, ComponentType type, boolean required) {
    }

    public enum ComponentType {
        HYPERDRIVE(ModItemTags.HYPERDRIVE_COMPONENT),
        LIFE_SUPPORT(ModItemTags.LIFE_SUPPORT_COMPONENT),
        REPULSORLIFT_GENERATOR(ModItemTags.REPULSORLIFT_GENERATOR_COMPONENT),
        NAVICOMPUTER(ModItemTags.NAVICOMPUTER_COMPONENT),
        MAIN_REACTOR(ModItemTags.MAIN_REACTOR_COMPONENT);


        private final TagKey<Item> tag;

        ComponentType(TagKey<Item> tag) {
            this.tag = tag;
        }

        public TagKey<Item> tag() {
            return tag;
        }
    }
}