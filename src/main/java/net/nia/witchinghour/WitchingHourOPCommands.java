package net.nia.witchinghour;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.Names;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.events.FoolMoon;
import org.joml.Vector3f;

import java.util.concurrent.CompletableFuture;

public class WitchingHourOPCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(
                            CommandManager.literal("witchinghour")

                                    // -------------------------
                                    // setBoltColor (ANYONE)
                                    // -------------------------
                                    .then(CommandManager.literal("setBoltColor")
                                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                                    .then(CommandManager.argument("r", FloatArgumentType.floatArg(0f, 1f))
                                                            .then(CommandManager.argument("g", FloatArgumentType.floatArg(0f, 1f))
                                                                    .then(CommandManager.argument("b", FloatArgumentType.floatArg(0f, 1f))
                                                                            .executes(ctx -> setBoltColor(
                                                                                    ctx.getSource(),
                                                                                    EntityArgumentType.getPlayer(ctx, "player"),
                                                                                    FloatArgumentType.getFloat(ctx, "r"),
                                                                                    FloatArgumentType.getFloat(ctx, "g"),
                                                                                    FloatArgumentType.getFloat(ctx, "b")
                                                                            ))
                                                                    )))))

                                    // Allow running without specifying a player → defaults to self
                                    .then(CommandManager.argument("r", FloatArgumentType.floatArg(0f, 1f))
                                            .then(CommandManager.argument("g", FloatArgumentType.floatArg(0f, 1f))
                                                    .then(CommandManager.argument("b", FloatArgumentType.floatArg(0f, 1f))
                                                            .executes(ctx -> setBoltColor(
                                                                    ctx.getSource(),
                                                                    ctx.getSource().getPlayer(),
                                                                    FloatArgumentType.getFloat(ctx, "r"),
                                                                    FloatArgumentType.getFloat(ctx, "g"),
                                                                    FloatArgumentType.getFloat(ctx, "b")
                                                            ))
                                                    )))

                                    // -------------------------
                                    // setName (ANYONE / OP)
                                    // -------------------------
                                    .then(CommandManager.literal("setName")
                                            // Everyone: set their own name
                                            .then(CommandManager.argument("first", StringArgumentType.word())
                                                    .suggests(WitchingHourOPCommands::suggestNames)

                                                    // /witchinghour setName <first>
                                                    .executes(ctx -> setName(
                                                            ctx.getSource(),
                                                            ctx.getSource().getPlayer(),
                                                            StringArgumentType.getString(ctx, "first"),
                                                            ""
                                                    ))

                                                    // /witchinghour setName <first> <last>
                                                    .then(CommandManager.argument("last", StringArgumentType.word())
                                                            .suggests(WitchingHourOPCommands::suggestNames)
                                                            .executes(ctx -> setName(
                                                                    ctx.getSource(),
                                                                    ctx.getSource().getPlayer(),
                                                                    StringArgumentType.getString(ctx, "first"),
                                                                    StringArgumentType.getString(ctx, "last")
                                                            )))
                                            )
                                    )

                                    // -------------------------
                                    // setStat (OPERATORS ONLY)
                                    // -------------------------
                                    .then(CommandManager.literal("setStat")
                                            .requires(src -> src.hasPermissionLevel(2)) // OP ONLY
                                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                                    .then(CommandManager.argument("stat", StringArgumentType.string())
                                                            .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                                                    .executes(ctx -> setStat(
                                                                            ctx.getSource(),
                                                                            EntityArgumentType.getPlayer(ctx, "player"),
                                                                            StringArgumentType.getString(ctx, "stat"),
                                                                            IntegerArgumentType.getInteger(ctx, "value")
                                                                    ))
                                                            ))))


                                    // -------------------------
                                    // unlockSpell (OPERATORS ONLY)
                                    // -------------------------
                                    .then(CommandManager.literal("unlockSpell")
                                            .requires(src -> src.hasPermissionLevel(2)) // OP ONLY
                                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                                    .then(CommandManager.argument("stat", StringArgumentType.string())
                                                            .then(CommandManager.argument("value", BoolArgumentType.bool())
                                                                    .executes(ctx -> unlockSpell(
                                                                            ctx.getSource(),
                                                                            EntityArgumentType.getPlayer(ctx, "player"),
                                                                            StringArgumentType.getString(ctx, "stat"),
                                                                            BoolArgumentType.getBool(ctx, "value")
                                                                    ))
                                                            ))))

                                    .then(CommandManager.literal("foolMoon")
                                            .requires(src -> src.hasPermissionLevel(2)) // OP only
                                            .then(CommandManager.literal("toggle")
                                                    .executes(ctx -> {
                                                        ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                        FoolMoon.toggle(player, true);

                                                        ctx.getSource().sendFeedback(
                                                                () -> Text.literal("§4☽ Fool Moon toggle invoked (force = true) §r"),
                                                                false
                                                        );

                                                        return 1;
                                                    })
                                            )
                                    )
            );
        });
    }

    private static int setStat(ServerCommandSource source, ServerPlayerEntity target, String stat, int value) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(target);
        data.setSpellLevel(stat, value);

        source.sendFeedback(
                () -> Text.literal("Set stat '" + stat + "' to " + value + " for " + target.getName().getString()),
                false
        );

        return 1;
    }

    private static int unlockSpell(ServerCommandSource source, ServerPlayerEntity target, String spell, boolean value) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(target);

        if (value) {
            data.unlockSpell(spell);
            source.sendFeedback(
                    () -> Text.literal("Unlocked spell: '" + spell + " for " + target.getName().getString()),
                    false
            );
        } else {
            data.lockSpell(spell);
            source.sendFeedback(
                    () -> Text.literal("Locked spell: '" + spell + " for " + target.getName().getString()),
                    false
            );
        }

        return 1;
    }

    private static int setBoltColor(ServerCommandSource source, ServerPlayerEntity target, float r, float g, float b) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(target);
        data.setBoltColor("Bolt Color", new Vector3f(r, g, b));

        source.sendFeedback(
                () -> Text.literal("Bolt color for " + target.getName().getString() +
                        " set to RGB: " + r + ", " + g + ", " + b),
                false
        );

        return 1;
    }

    private static CompletableFuture<Suggestions> suggestNames(
            CommandContext<ServerCommandSource> context,
            SuggestionsBuilder builder) {

        for (String name : Names.names) {
            builder.suggest(name);
        }

        return builder.buildFuture();
    }

    private static int setName(ServerCommandSource source,
                               ServerPlayerEntity target,
                               String first,
                               String last) {

        boolean firstOnly = source.getServer()
                .getGameRules()
                .getBoolean(ModGameRules.FIRST_NAMES_ONLY);

        if (!firstOnly && last.isEmpty()) {
            source.sendError(Text.literal(
                    "This world requires both a first and last name."
            ));
            return 0;
        }

        if (!Names.setName(target, first, last)) {
            source.sendError(Text.literal(
                    "That name is invalid, already taken, or the player already has a name."
            ));
            return 0;
        }

        source.sendFeedback(
                () -> Text.literal(
                        "Set "
                                + target.getName().getString()
                                + "'s name to "
                                + (firstOnly ? first : first + " " + last)
                ),
                false
        );

        return 1;
    }
}