package me.drex.meliuscommands.config.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import me.drex.meliuscommands.MeliusCommands;
import me.drex.meliuscommands.config.common.CommandAction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class CommandNode<T extends ArgumentBuilder<CommandSourceStack, T>> {

    public final String id;
    public final List<LiteralNode> literals;
    public final List<ArgumentNode<?>> arguments;
    public final Optional<MinecraftPredicate> requires;
    public final List<CommandAction> executions;
    public final Optional<String> redirect;

    protected CommandNode(String id, List<LiteralNode> literals, List<ArgumentNode<?>> arguments, Optional<MinecraftPredicate> requires, List<CommandAction> executions, Optional<String> redirect) {
        this.id = id;
        this.literals = literals;
        this.arguments = arguments;
        this.requires = requires;
        this.executions = executions;
        this.redirect = redirect;
    }

    public T build(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        T builder = getArgumentBuilder(buildContext);
        redirect.ifPresent(redirectPath -> {
            com.mojang.brigadier.tree.CommandNode<CommandSourceStack> dispatcherNode = dispatcher.findNode(Arrays.asList(redirectPath.split("\\.")));
            if (dispatcherNode == null) {
                MeliusCommands.LOGGER.error("Failed to find node with path '{}' for redirecting", redirectPath);
            } else {
                builder
                    .requires(dispatcherNode.getRequirement())
                    .forward(dispatcherNode.getRedirect(), dispatcherNode.getRedirectModifier(), dispatcherNode.isFork())
                    .executes(dispatcherNode.getCommand());
                for (final com.mojang.brigadier.tree.CommandNode<CommandSourceStack> child : dispatcherNode.getChildren()) {
                    builder.then(child);
                }
            }
        });
        requires.ifPresent(minecraftPredicate -> {
            builder.requires(source -> minecraftPredicate.test(PredicateContext.of(source)).success());
        });
        if (!executions.isEmpty()) {
            builder.executes(executionContext -> {
                int result = 0;
                for (CommandAction action : executions) {
                    result += action.execute(executionContext);
                }
                return result;
            });
        }
        for (LiteralNode literal : literals) {
            builder.then(literal.build(dispatcher, buildContext));
        }
        for (ArgumentNode<?> argument : arguments) {
            builder.then(argument.build(dispatcher, buildContext));
        }
        return builder;
    }

    abstract T getArgumentBuilder(CommandBuildContext context);

}