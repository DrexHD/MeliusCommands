package me.drex.meliuscommands.parser;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandBuildContext;

public interface ArgumentTypeParser {

    boolean canParse(String namespace, String path);

    ArgumentType<?> parse(CommandBuildContext context, String namespace, String path, String args);

}
