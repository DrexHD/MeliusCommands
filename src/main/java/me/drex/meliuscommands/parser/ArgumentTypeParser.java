package me.drex.meliuscommands.parser;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.resources.Identifier;

public interface ArgumentTypeParser {

    boolean canParse(Identifier resourceLocation);

    ArgumentType<?> parse(CommandBuildContext context, Identifier resourceLocation, String args);

}
