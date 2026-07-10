package net.netherway.starwarschaincode.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = "starwarschaincode", value = Dist.CLIENT)
public class ModKeyMappings {

    public static final KeyMapping RACE_ABILITY_1 = new KeyMapping(
            "key.starwarschaincode.race_ability_1",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_R,
            "key.categories.starwarschaincode"
    );

    public static final KeyMapping RACE_ABILITY_2 = new KeyMapping(
            "key.starwarschaincode.race_ability_2",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_J,
            "key.categories.starwarschaincode"
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(RACE_ABILITY_1);
        event.register(RACE_ABILITY_2);
    }
}