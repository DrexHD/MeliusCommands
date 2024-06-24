package me.drex.meliuscommands.config.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import me.drex.meliuscommands.config.common.CommandAction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public abstract class CommandNode<T extends ArgumentBuilder<CommandSourceStack, T>> {

    public final String id;
    public final List<LiteralNode> literals;
    public final List<ArgumentNode<?>> arguments;
    public final MinecraftPredicate requires;
    public final List<CommandAction> executions;

    protected CommandNode(String id, List<LiteralNode> literals, List<ArgumentNode<?>> arguments, MinecraftPredicate requires, List<CommandAction> executions) {
        this.id = id;
        this.literals = literals;
        this.arguments = arguments;
        this.requires = requires;
        this.executions = executions;
    }

    public T build(CommandBuildContext buildContext) {
        T builder = getArgumentBuilder(buildContext);
        builder.requires(source -> requires.test(PredicateContext.of(source)).success());
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
            builder.then(literal.build(buildContext));
        }
        for (ArgumentNode<?> argument : arguments) {
            builder.then(argument.build(buildContext));
        }
        return builder;
    }

    abstract T getArgumentBuilder(CommandBuildContext context);

}