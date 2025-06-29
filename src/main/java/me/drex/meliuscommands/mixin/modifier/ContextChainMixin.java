package me.drex.meliuscommands.mixin.modifier;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import me.drex.meliuscommands.config.ConfigManager;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcher;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifier;
import me.drex.meliuscommands.config.modifier.execution.is_executable.IsExecutableModifier;
import me.drex.meliuscommands.config.common.CommandAction;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContextChain.class)
public abstract class ContextChainMixin {

    @WrapOperation(
        method = "runExecutable",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/Command;run(Lcom/mojang/brigadier/context/CommandContext;)I"
        ),
        remap = false
    )
    private static <S> int runConditionally(Command<S> command, CommandContext<S> ctx, Operation<Integer> original) throws CommandSyntaxException {
        S src = ctx.getSource();
        if (!(src instanceof CommandSourceStack source)) {
            return original.call(command, ctx);
        }
        //noinspection unchecked
        CommandContext<CommandSourceStack> context = (CommandContext<CommandSourceStack>) ctx;

        for (CommandMatcher executionMatcher : ConfigManager.COMMAND_EXECUTION_MATCHERS) {
            if (!executionMatcher.matches(context)) {
                continue;
            }
            for (ExecutionModifier modifier : executionMatcher.executionModifiers()) {
                if (!(modifier instanceof IsExecutableModifier isExecutableModifier)) {
                    continue;
                }
                if (!isExecutableModifier.isExecutable(context)) {
                    int result = 0;
                    for (CommandAction commandAction : isExecutableModifier.failure()) {
                        result += commandAction.execute(context);
                    }
                    return result;
                }
            }
        }
        for (CommandMatcher executionMatcher : ConfigManager.COMMAND_EXECUTION_MATCHERS) {
            if (!executionMatcher.matches(context)) {
                continue;
            }
            for (ExecutionModifier executionModifier : executionMatcher.executionModifiers()) {
                executionModifier.onSuccess(context);
            }
        }
        return original.call(command, ctx);
    }

}
