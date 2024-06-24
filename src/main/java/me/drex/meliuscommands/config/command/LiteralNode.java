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

public class LiteralNode extends CommandNode<LiteralArgumentBuilder<CommandSourceStack>> {

    public static final Codec<LiteralNode> CODEC = Codec.lazyInitialized(() -> Codec.recursive("Literal Node", literalCodec -> RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(node -> node.id),
            literalCodec.listOf().optionalFieldOf("literals", Collections.emptyList()).forGetter(node -> node.literals),
            ArgumentNode.CODEC.listOf().optionalFieldOf("arguments", Collections.emptyList()).forGetter(node -> node.arguments),
            PredicateRegistry.CODEC.optionalFieldOf("require", BuiltinPredicates.operatorLevel(0)).forGetter(node -> node.requires),
            CommandAction.CODEC.listOf().optionalFieldOf("executes", Collections.emptyList()).forGetter(node -> node.executions)
        ).apply(instance, LiteralNode::new))));

    protected LiteralNode(String id, List<LiteralNode> literals, List<ArgumentNode<?>> arguments, MinecraftPredicate require, List<CommandAction> actions) {
        super(id, literals, arguments, require, actions);
    }

    @Override
    LiteralArgumentBuilder<CommandSourceStack> getArgumentBuilder(CommandBuildContext context) {
        return Commands.literal(id);
    }
}
