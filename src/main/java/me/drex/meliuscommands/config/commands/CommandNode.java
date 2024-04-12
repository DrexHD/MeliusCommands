package me.drex.meliuscommands.config.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public abstract class CommandNode<T extends ArgumentBuilder<CommandSourceStack, T>> {

    public String id = null;

    public Execution[] executes = new Execution[]{};

    public MinecraftPredicate require = BuiltinPredicates.operatorLevel(0);

    public LiteralNode[] literals = new LiteralNode[]{};

    public ArgumentNode<?>[] arguments = new ArgumentNode[]{};

    public T build(CommandBuildContext context) {
        T builder = getArgumentBuilder(context);
        builder.requires(src -> require.test(PredicateContext.of(src)).success());
        if (executes.length > 0) builder.executes(ctx -> {
            int result = 0;
            for (Execution execution : executes) {
                result += execution.execute(ctx);
            }
            return result;
        });
        for (LiteralNode literal : literals) {
            builder.then(literal.build(context));
        }
        for (ArgumentNode<?> argument : arguments) {
            builder.then(argument.build(context));
        }
        return builder;
    }

    abstract T getArgumentBuilder(CommandBuildContext context);

}