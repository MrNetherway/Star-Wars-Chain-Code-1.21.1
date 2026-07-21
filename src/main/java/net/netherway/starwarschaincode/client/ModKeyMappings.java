package net.netherway.starwarschaincode.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

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

    public static final KeyMapping LIGHTSABER_ACTIVATE = new KeyMapping(
            "key.starwarschaincode.lightsaber_activate",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_V,
            "key.categories.starwarschaincode"
    );

    public static final KeyMapping LIGHTSABER_IMPULSE = new KeyMapping(
    "key.starwarschaincode.lightsaber_impulse",
    KeyConflictContext.IN_GAME,
    InputConstants.Type.MOUSE,
    InputConstants.KEY_B,
    "key.categories.starwarschaincode"
    );

    public static final KeyMapping SHIP_DESCEND = new KeyMapping(
            "key.starwarschaincode.ship_descend",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_LCONTROL,
            "key.categories.starwarschaincode"
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(RACE_ABILITY_1);
        event.register(RACE_ABILITY_2);
        event.register(LIGHTSABER_ACTIVATE);
        event.register(LIGHTSABER_IMPULSE);
        event.register(SHIP_DESCEND);
    }
}