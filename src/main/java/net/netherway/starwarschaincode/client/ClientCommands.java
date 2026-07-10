package net.netherway.starwarschaincode.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = "starwarschaincode", value = Dist.CLIENT)
public class ClientCommands {
    @SubscribeEvent
    public static void onRegister(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("racemenu")
                        .executes(ClientCommands::open)
        );
    }

    private static int open(CommandContext<CommandSourceStack> ctx) {
        Minecraft.getInstance().setScreen(new RaceSelectScreen());
        return 1;
    }
}