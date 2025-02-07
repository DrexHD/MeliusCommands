package me.drex.meliuscommands.config.common;

import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import me.drex.meliuscommands.mixin.CommandContextAccessor;
import me.drex.meliuscommands.util.CodecUtil;
import net.minecraft.commands.CommandSourceStack;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;

public record CommandAction(String command, boolean console, boolean silent, Optional<Integer> permissionLevel) {

    private static final BinaryOperator<ResultConsumer<CommandSourceStack>> CALLBACK_CHAINER = (resultConsumer, resultConsumer2) -> (commandContext, bl, i) -> {
        resultConsumer.onCommandComplete(commandContext, bl, i);
        resultConsumer2.onCommandComplete(commandContext, bl, i);
    };

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
    public static final Codec<CommandAction> CODEC = CodecUtil.withAlternative(FULL_CODEC, Codec.STRING, CommandAction::new);

    public int execute(CommandContext<CommandSourceStack> ctx) {
        @SuppressWarnings("unchecked")
        Map<String, ParsedArgument<CommandSourceStack, ?>> arguments = ((CommandContextAccessor<CommandSourceStack>) ctx).getArguments();
        var command = this.command;
        for (Map.Entry<String, ParsedArgument<CommandSourceStack, ?>> entry : arguments.entrySet()) {
            String value = entry.getValue().getRange().get(ctx.getInput() + " ");
            command = command.replace("${" + entry.getKey() + "}", value);
        }

        var placeholderContext = PlaceholderContext.of(ctx.getSource());

        String parsedCommand = Placeholders.parseText(TextNode.of(command), placeholderContext).getString();

        AtomicInteger result = new AtomicInteger();

        CommandSourceStack modifiedSource = ctx.getSource()
            .withCallback((context, success, i) -> {
                result.set(i);
            }, CALLBACK_CHAINER);
        if (console) {
            modifiedSource = modifiedSource.withSource(ctx.getSource().getServer());
        }
        if (silent) {
            modifiedSource = modifiedSource.withSuppressedOutput();
        }
        if (permissionLevel.isPresent()) {
            modifiedSource = modifiedSource.withPermission(permissionLevel.get());
        }

        ctx.getSource().getServer().getCommands().performPrefixedCommand(modifiedSource, parsedCommand);
        return result.get();
    }


}
