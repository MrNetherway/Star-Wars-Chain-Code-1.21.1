package net.netherway.starwarschaincode.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.WorldGenLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.worldgen.asteroid.AsteroidGenerator;


@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID, value = Dist.CLIENT)
public class ClientCommands {


    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("racemenu")
                        .executes(ClientCommands::open)
        );

    }

    private static int open(CommandContext<CommandSourceStack> ctx) {
        Minecraft.getInstance().execute(() ->
                Minecraft.getInstance().setScreen(new RaceSelectScreen())
        );
        return 1;
    }

}