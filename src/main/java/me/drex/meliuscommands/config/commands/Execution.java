package me.drex.meliuscommands.config.commands;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.meliuscommands.mixin.CommandContextAccessor;
import net.minecraft.commands.CommandSourceStack;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Execution {

    public String command = null;

    public boolean silent = false;

    @SerializedName("as_console")
    public boolean asConsole = false;

    @SerializedName("op_level")
    public Integer opLevel = null;

    public int execute(CommandContext<CommandSourceStack> ctx) {
        if (command == null) return 0;
        @SuppressWarnings("unchecked")
        Map<String, ParsedArgument<CommandSourceStack, ?>> arguments = ((CommandContextAccessor<CommandSourceStack>) ctx).getArguments();
        String command = this.command;
        for (Map.Entry<String, ParsedArgument<CommandSourceStack, ?>> entry : arguments.entrySet()) {
            String argumentValue = entry.getValue().getRange().get(ctx.getInput() + " ");
            command = command.replace("${" + entry.getKey() + "}", argumentValue);
        }
        CommandSourceStack source;
        if (asConsole) {
            source = ctx.getSource().getServer().createCommandSourceStack().withEntity(ctx.getSource().getEntity());
        } else {
            source = ctx.getSource();
        }
        if (opLevel != null) source = source.withMaximumPermission(opLevel);
        if (silent) source = source.withSuppressedOutput();
        AtomicInteger result = new AtomicInteger();

        source.withCallback((context, bl, i) -> {
            result.getAndIncrement();
        });
        ctx.getSource().getServer().getCommands().performPrefixedCommand(source, command);
        return result.get();
    }

}
