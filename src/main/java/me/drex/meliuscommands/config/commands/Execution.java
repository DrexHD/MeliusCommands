package me.drex.meliuscommands.config.commands;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.meliuscommands.mixin.CommandContextAccessor;
import net.minecraft.commands.CommandSourceStack;

import java.util.Map;

public class Execution {

    public String command = null;

    public boolean silent = false;

    @SerializedName("as_console")
    public boolean asConsole = false;

    @SerializedName("op_level")
    public Integer opLevel = null;

    public int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (command == null) return 0;
        @SuppressWarnings("unchecked")
        Map<String, ParsedArgument<CommandSourceStack, ?>> arguments = ((CommandContextAccessor<CommandSourceStack>) ctx).getArguments();
        CommandDispatcher<CommandSourceStack> dispatcher = ctx.getSource().getServer().getCommands().getDispatcher();
        String command = this.command;
        for (Map.Entry<String, ParsedArgument<CommandSourceStack, ?>> entry : arguments.entrySet()) {
            String argumentValue = entry.getValue().getRange().get(ctx.getInput() + " ");
            command = command.replaceAll("\\$\\{" + entry.getKey() + "}", argumentValue);
        }
        CommandSourceStack source = asConsole ? ctx.getSource().getServer().createCommandSourceStack() : ctx.getSource();
        if (opLevel != null) source = source.withMaximumPermission(opLevel);
        if (silent) source = source.withSuppressedOutput();
        return dispatcher.execute(command, source);
    }

}
