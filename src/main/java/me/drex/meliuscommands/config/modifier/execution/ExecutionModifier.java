package me.drex.meliuscommands.config.modifier.execution;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public interface ExecutionModifier {
    ExecutionModifierType<?> getType();

    default void onSuccess(CommandContext<CommandSourceStack> context) {
    }
}
