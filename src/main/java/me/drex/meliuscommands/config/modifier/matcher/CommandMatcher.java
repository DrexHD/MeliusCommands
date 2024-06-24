package me.drex.meliuscommands.config.modifier.matcher;

import com.mojang.brigadier.context.CommandContext;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifier;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public interface CommandMatcher {
    boolean matches(CommandContext<CommandSourceStack> context);

    List<ExecutionModifier> executionModifiers();

    CommandMatcherType<?> getType();
}
