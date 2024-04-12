package me.drex.meliuscommands.config.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class LiteralNode extends CommandNode<LiteralArgumentBuilder<CommandSourceStack>> {

    @Override
    LiteralArgumentBuilder<CommandSourceStack> getArgumentBuilder(CommandBuildContext commandBuildContext) {
        return Commands.literal(id);
    }
}
