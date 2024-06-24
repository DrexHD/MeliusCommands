package me.drex.meliuscommands.config.modifier.execution.is_executable;

import com.mojang.brigadier.context.CommandContext;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifier;
import me.drex.meliuscommands.config.common.CommandAction;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public interface IsExecutableModifier extends ExecutionModifier {
    List<CommandAction> failure();

    boolean isExecutable(CommandContext<CommandSourceStack> context);
}
