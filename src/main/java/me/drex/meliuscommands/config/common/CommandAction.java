package me.drex.meliuscommands.config.common;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.meliuscommands.mixin.CommandContextAccessor;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public record CommandAction(String command, boolean console, boolean silent, Optional<Integer> permissionLevel) {

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

    public int execute(CommandContext<CommandSourceStack> ctx) {
        @SuppressWarnings("unchecked")
        Map<String, ParsedArgument<CommandSourceStack, ?>> arguments = ((CommandContextAccessor<CommandSourceStack>) ctx).getArguments();
        String modifiedCommand = command;
        for (Map.Entry<String, ParsedArgument<CommandSourceStack, ?>> entry : arguments.entrySet()) {
            String argumentValue = entry.getValue().getRange().get(ctx.getInput() + " ");
            modifiedCommand = modifiedCommand.replace("${" + entry.getKey() + "}", argumentValue);
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
            modifiedSource = modifiedSource.withPermission(permissionLevel.get());
        }

        ctx.getSource().getServer().getCommands().performPrefixedCommand(modifiedSource, modifiedCommand);
        return result.get();
    }


}
