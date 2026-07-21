package net.netherway.starwarschaincode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.faction.Faction;
import net.netherway.starwarschaincode.faction.FactionReputationHelper;

@EventBusSubscriber(modid = StarWarsChainCode.MOD_ID)
public class ModCommands {

    private static final SuggestionProvider<CommandSourceStack> FACTION_SUGGESTIONS = (context, builder) -> {
        for (Faction faction : Faction.values()) {
            builder.suggest(faction.getSerializedName());
        }
        return builder.buildFuture();
    };

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("getreputation")
                .requires(source -> source.getEntity() instanceof ServerPlayer)
                .then(Commands.argument("faction", StringArgumentType.word())
                        .suggests(FACTION_SUGGESTIONS)
                        .executes(ModCommands::executeGetReputation)));

        dispatcher.register(Commands.literal("setreputation")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("faction", StringArgumentType.word())
                        .suggests(FACTION_SUGGESTIONS)
                        .then(Commands.argument("quantity", IntegerArgumentType.integer())
                                .executes(ModCommands::executeSetReputation))));
    }

    private static int executeGetReputation(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
            return 0;
        }

        Faction faction = resolveFaction(ctx);
        if (faction == null) {
            ctx.getSource().sendFailure(Component.literal("Facção inválida."));
            return 0;
        }

        int reputation = FactionReputationHelper.getFactionReputation(player, faction);
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Reputação com " + faction.getDisplayName().getString() + ": " + reputation
        ), false);

        return reputation;
    }

    private static int executeSetReputation(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
            ctx.getSource().sendFailure(Component.literal("Este comando só pode ser usado por um jogador."));
            return 0;
        }

        Faction faction = resolveFaction(ctx);
        if (faction == null) {
            ctx.getSource().sendFailure(Component.literal("Facção inválida."));
            return 0;
        }

        int quantity = IntegerArgumentType.getInteger(ctx, "quantity");
        FactionReputationHelper.setReputation(player, faction, quantity);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "Reputação com " + faction.getDisplayName().getString() + " definida para " + quantity
        ), true);

        return quantity;
    }

    private static Faction resolveFaction(CommandContext<CommandSourceStack> ctx) {
        String id = StringArgumentType.getString(ctx, "faction");
        for (Faction faction : Faction.values()) {
            if (faction.getSerializedName().equals(id)) {
                return faction;
            }
        }
        return null;
    }
}