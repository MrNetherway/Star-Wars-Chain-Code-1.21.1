package net.netherway.starwarschaincode.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.netherway.starwarschaincode.StarWarsChainCode;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, StarWarsChainCode.MOD_ID);

    public static final Supplier<SoundEvent> WOOKIEE_ABILITY_1 = registerSoundEffect("wookiee_ability_1");


    private static Supplier<SoundEvent> registerSoundEffect(String name){
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(StarWarsChainCode.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus)
    {
        SOUND_EVENTS.register(eventBus);
    }
}
