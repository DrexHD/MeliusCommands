package me.drex.meliuscommands.parser;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.resources.ResourceLocation;

public interface ArgumentTypeParser {

    boolean canParse(ResourceLocation resourceLocation);

    ArgumentType<?> parse(CommandBuildContext context, ResourceLocation resourceLocation, String args);

}
