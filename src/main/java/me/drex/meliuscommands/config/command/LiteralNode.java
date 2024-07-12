package me.drex.meliuscommands.config.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateRegistry;
import me.drex.meliuscommands.config.common.CommandAction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LiteralNode extends CommandNode<LiteralArgumentBuilder<CommandSourceStack>> {

    public static final Codec<LiteralNode> CODEC = Codec.lazyInitialized(() -> Codec.recursive("Literal Node", literalCodec -> RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(node -> node.id),
            literalCodec.listOf().optionalFieldOf("literals", Collections.emptyList()).forGetter(node -> node.literals),
            ArgumentNode.CODEC.listOf().optionalFieldOf("arguments", Collections.emptyList()).forGetter(node -> node.arguments),
            PredicateRegistry.CODEC.optionalFieldOf("require").forGetter(node -> node.requires),
            CommandAction.CODEC.listOf().optionalFieldOf("executes", Collections.emptyList()).forGetter(node -> node.executions),
            Codec.STRING.optionalFieldOf("redirect").forGetter(node -> node.redirect)
        ).apply(instance, LiteralNode::new))));

    protected LiteralNode(String id, List<LiteralNode> literals, List<ArgumentNode<?>> arguments, Optional<MinecraftPredicate> require, List<CommandAction> actions, Optional<String> redirect) {
        super(id, literals, arguments, require, actions, redirect);
    }

    @Override
    LiteralArgumentBuilder<CommandSourceStack> getArgumentBuilder(CommandBuildContext context) {
        return Commands.literal(id);
    }
}
