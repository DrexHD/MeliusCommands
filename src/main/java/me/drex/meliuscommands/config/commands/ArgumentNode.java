package me.drex.meliuscommands.config.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.drex.meliuscommands.parser.ArgumentTypeParser;
import me.drex.meliuscommands.parser.BrigadierArgumentTypeParser;
import me.drex.meliuscommands.parser.MinecraftArgumentTypeParser;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;

public class ArgumentNode<T> extends CommandNode<RequiredArgumentBuilder<CommandSourceStack, T>> {

    public String type;

    private static final ArgumentTypeParser[] PARSERS = new ArgumentTypeParser[]{BrigadierArgumentTypeParser.INSTANCE, MinecraftArgumentTypeParser.INSTANCE};

    @Override
    RequiredArgumentBuilder<CommandSourceStack, T> getArgumentBuilder(CommandBuildContext context) {
        return Commands.argument(id, getArgumentType(context));
    }

    private ArgumentType<T> getArgumentType(CommandBuildContext context) {
        if (type == null)
            throw new IllegalArgumentException("Argument type not defined for " + id);
        String[] splits = type.split(" ", 2);
        String type = splits[0];
        String args = splits.length > 1 ? splits[1] : "";
        ResourceLocation resourceLocation = new ResourceLocation(type);
        for (ArgumentTypeParser parser : PARSERS) {
            if (parser.canParse(resourceLocation)) {
                @SuppressWarnings("unchecked")
                ArgumentType<T> parse = (ArgumentType<T>) parser.parse(context, resourceLocation, args);
                return parse;
            }
        }
        throw new IllegalArgumentException("Unknown argument type: " + type);
    }
}