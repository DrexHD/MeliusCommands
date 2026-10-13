package me.drex.meliuscommands.config.common;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.placeholders.api.ParserContext;
//? if >= 26.1 {
import eu.pb4.placeholders.api.ServerPlaceholderContext;
//? } else {
//import eu.pb4.placeholders.api.PlaceholderContext;
//? }
import eu.pb4.placeholders.api.node.DynamicTextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import me.drex.meliuscommands.mixin.CommandContextAccessor;
import me.drex.meliuscommands.util.CommandDelayManager;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
//? if > 1.21.10 {
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
//? }

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static eu.pb4.placeholders.api.parsers.TagLikeParser.PLACEHOLDER_USER;

public record CommandAction(String command, boolean console, boolean silent, Optional<Integer> permissionLevel, int delayTicks) {

    private static final TagLikeParser.Format PLACEHOLDER_COMMAND = TagLikeParser.Format.of("${", "}", " ");
    private static final ParserContext.Key<Function<String, Component>> ARGUMENTS = DynamicTextNode.key("melius_commands");
    private static final NodeParser PARSER = NodeParser.builder()
        //? if >= 26.1 {
        .serverPlaceholders(PLACEHOLDER_COMMAND)
        //? } else {
//        .globalPlaceholders(PLACEHOLDER_COMMAND)
        //? }
        .placeholders(PLACEHOLDER_USER, ARGUMENTS)
        .build();

    public CommandAction(String command) {
        this(command, true, true, Optional.of(4), 0);
    }

    public CommandAction {
        delayTicks = Math.max(0, delayTicks);
    }

    public static final Codec<CommandAction> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("command").forGetter(CommandAction::command),
            Codec.BOOL.optionalFieldOf("as_console", true).forGetter(CommandAction::console),
            Codec.BOOL.optionalFieldOf("silent", true).forGetter(CommandAction::silent),
            Codec.INT.optionalFieldOf("op_level").forGetter(CommandAction::permissionLevel),
            Codec.INT.optionalFieldOf("delay_ticks", 0).forGetter(CommandAction::delayTicks)
        ).apply(instance, CommandAction::new)
    );
    public static final Codec<CommandAction> CODEC = Codec.withAlternative(FULL_CODEC, Codec.STRING, CommandAction::new);

    public static int executeAll(CommandContext<CommandSourceStack> ctx, List<CommandAction> actions) throws CommandSyntaxException {
        if (actions.isEmpty()) {
            return 0;
        }

        AtomicInteger result = new AtomicInteger();
        PreparedAction[] preparedActions = new PreparedAction[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            preparedActions[i] = actions.get(i).prepare(ctx, result);
        }

        scheduleSequential(preparedActions, 0);

        for (PreparedAction action : preparedActions) {
            if (action.delayTicks() > 0) {
                return 1;
            }
        }
        return result.get();
    }

    public int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return executeAll(ctx, List.of(this));
    }

    private static void scheduleSequential(PreparedAction[] actions, int index) {
        if (index >= actions.length) {
            return;
        }

        PreparedAction action = actions[index];
        Runnable runNext = () -> {
            action.runner().run();
            scheduleSequential(actions, index + 1);
        };

        CommandDelayManager.schedule(action.delayTicks(), runNext);
    }

    private PreparedAction prepare(CommandContext<CommandSourceStack> ctx, AtomicInteger result) throws CommandSyntaxException {
        @SuppressWarnings("unchecked")
        Map<String, ParsedArgument<CommandSourceStack, ?>> arguments = ((CommandContextAccessor<CommandSourceStack>) ctx).getArguments();
        String parsedCommand;
        try {
            var parserContext = /*? if >= 26.1 {*/ServerPlaceholderContext/*?} else {*//*PlaceholderContext*//*?}*/.of(ctx.getSource()).asParserContext().with(ARGUMENTS, input -> {
                var argument = arguments.get(input);
                if (argument == null) {
                    return Component.literal("${" + input + "}");
                }
                String value = argument.getRange().get(ctx.getInput() + " ");
                return Component.literal(value);
            });
            parsedCommand = PARSER./*? if >= 26.1 {*/parseComponent/*?} else {*//*parseText*//*?}*/(command.replace("\\", "\\\\"), parserContext).getString();
        } catch (IllegalStateException e) {
            if (e.getCause() instanceof CommandSyntaxException syntaxException) {
                throw syntaxException;
            } else {
                throw new SimpleCommandExceptionType(new LiteralMessage("Failed to parse '" + command + "', because '" + e.getMessage() + "'")).create();
            }
        }

        CommandSourceStack modifiedSource = ctx.getSource().withCallback((bl, i) -> {
            result.addAndGet(i);
        }, CommandResultCallback::chain);
        if (console) {
            modifiedSource = modifiedSource.withSource(ctx.getSource().getServer());
        }
        if (silent) {
            modifiedSource = modifiedSource.withSuppressedOutput();
        }
        if (permissionLevel.isPresent()) {
            //? if > 1.21.10 {
            modifiedSource = modifiedSource.withPermission(LevelBasedPermissionSet.forLevel(PermissionLevel.byId(permissionLevel.get())));
            //? } else {
            /*modifiedSource = modifiedSource.withPermission(permissionLevel.get());
            *///? }
        }

        CommandSourceStack finalSource = modifiedSource;
        Runnable runner = () -> ctx.getSource().getServer().getCommands().performPrefixedCommand(finalSource, parsedCommand);
        return new PreparedAction(delayTicks, runner);
    }

    private record PreparedAction(int delayTicks, Runnable runner) {
    }

}
