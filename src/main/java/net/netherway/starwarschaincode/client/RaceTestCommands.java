package net.netherway.starwarschaincode.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.netherway.starwarschaincode.race.Race;
import net.netherway.starwarschaincode.race.RaceAttachments;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = "starwarschaincode")
public class RaceTestCommands {

    @SubscribeEvent
    public static void onRegister(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("setrace").<CommandSourceStack>
                        executes(RaceTestCommands::setRace)
        );
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("getrace").<CommandSourceStack>
                        executes(RaceTestCommands::getRace)
        );
    }

    private static int setRace(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        player.setData(RaceAttachments.PLAYER_RACE, Race.WOOKIEE);
        ctx.getSource().sendSuccess(() -> Component.literal("Raça definida: Wookiee"), false);
        return 1;
    }

    private static int getRace(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Race race = player.getData(RaceAttachments.PLAYER_RACE);
        ctx.getSource().sendSuccess(() -> Component.literal("Sua raça: " + race.getName()), false);
        return 1;
    }
}