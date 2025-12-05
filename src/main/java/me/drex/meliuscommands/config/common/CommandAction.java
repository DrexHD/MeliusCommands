package me.drex.meliuscommands.config.common;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.DynamicTextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import me.drex.meliuscommands.mixin.CommandContextAccessor;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
//? if > 1.21.10 {
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
//? }

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static eu.pb4.placeholders.api.parsers.TagLikeParser.PLACEHOLDER_USER;

public record CommandAction(String command, boolean console, boolean silent, Optional<Integer> permissionLevel) {

    private static final TagLikeParser.Format PLACEHOLDER_COMMAND = TagLikeParser.Format.of("${", "}", " ");
    private static final ParserContext.Key<Function<String, Component>> ARGUMENTS = DynamicTextNode.key("melius_commands");
    private static final NodeParser PARSER = NodeParser.builder()
        .globalPlaceholders(PLACEHOLDER_COMMAND)
        .placeholders(PLACEHOLDER_USER, ARGUMENTS)
        .build();

    public CommandAction(String command) {
        this(command, true, true, Optional.of(4));
    }

    public static final Codec<CommandAction> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("command").forGetter(CommandAction::command),
            Codec.BOOL.optionalFieldOf("as_console", true).forGetter(CommandAction::console),
            Codec.BOOL.optionalFieldOf("silent", true).forGetter(CommandAction::silent),
            Codec.INT.optionalFieldOf("op_level").forGetter(CommandAction::permissionLevel)
        ).apply(instance, CommandAction::new)
    );
    public static final Codec<CommandAction> CODEC = Codec.withAlternative(FULL_CODEC, Codec.STRING, CommandAction::new);

    public int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        @SuppressWarnings("unchecked")
        Map<String, ParsedArgument<CommandSourceStack, ?>> arguments = ((CommandContextAccessor<CommandSourceStack>) ctx).getArguments();
        String parsedCommand;
        try {
            var parserContext = PlaceholderContext.of(ctx.getSource()).asParserContext().with(ARGUMENTS, input -> {
                var argument = arguments.get(input);
                if (argument == null) {
                    throw new IllegalStateException(new SimpleCommandExceptionType(new LiteralMessage("Unknown argument '" + input + "' in '" + command + "'")).create());
                }
                String value = argument.getRange().get(ctx.getInput() + " ");
                return Component.literal(value);
            });
            parsedCommand = PARSER.parseText(command.replace("\\", "\\\\"), parserContext).getString();
        } catch (IllegalStateException e) {
            if (e.getCause() instanceof CommandSyntaxException syntaxException) {
                throw syntaxException;
            } else {
                throw new SimpleCommandExceptionType(new LiteralMessage("Failed to parse '" + command + "', because '" + e.getMessage() + "'")).create();
            }
        }

        AtomicInteger result = new AtomicInteger();

        CommandSourceStack modifiedSource = ctx.getSource().withCallback((bl, i) -> {
            result.set(i);
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

        ctx.getSource().getServer().getCommands().performPrefixedCommand(modifiedSource, parsedCommand);
        return result.get();
    }


}
