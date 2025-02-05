package me.drex.meliuscommands.config.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateRegistry;
import me.drex.meliuscommands.parser.ArgumentTypeParser;
import me.drex.meliuscommands.parser.BrigadierArgumentTypeParser;
import me.drex.meliuscommands.parser.MinecraftArgumentTypeParser;
import me.drex.meliuscommands.config.common.CommandAction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ArgumentNode<T> extends CommandNode<RequiredArgumentBuilder<CommandSourceStack, T>> {

    public final String type;

    public static final Codec<ArgumentNode<?>> CODEC = Codec.lazyInitialized(() -> Codec.recursive("Argument Node", argumentCodec -> RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(node -> node.id),
            Codec.STRING.fieldOf("type").forGetter(node -> node.type),
            LiteralNode.CODEC.listOf().optionalFieldOf("literals", Collections.emptyList()).forGetter(node -> node.literals),
            argumentCodec.listOf().optionalFieldOf("arguments", Collections.emptyList()).forGetter(node -> node.arguments),
            PredicateRegistry.CODEC.optionalFieldOf("require").forGetter(node -> node.requires),
            CommandAction.CODEC.listOf().optionalFieldOf("executes", Collections.emptyList()).forGetter(node -> node.executions),
            Codec.STRING.optionalFieldOf("redirect").forGetter(node -> node.redirect)

        ).apply(instance, ArgumentNode::new))));

    private static final ArgumentTypeParser[] PARSERS = new ArgumentTypeParser[]{BrigadierArgumentTypeParser.INSTANCE, MinecraftArgumentTypeParser.INSTANCE};

    protected ArgumentNode(String id, String type, List<LiteralNode> literals, List<ArgumentNode<?>> arguments, Optional<MinecraftPredicate> require, List<CommandAction> actions, Optional<String> redirect) {
        super(id, literals, arguments, require, actions, redirect);
        this.type = type;
    }

    @Override
    RequiredArgumentBuilder<CommandSourceStack, T> getArgumentBuilder(CommandBuildContext context) {
        return Commands.argument(id, getArgumentType(context));
    }

    private ArgumentType<T> getArgumentType(CommandBuildContext context) {
        String[] splits = type.split(" ", 2);
        String type = splits[0];
        String args = splits.length > 1 ? splits[1] : "";
        ResourceLocation resourceLocation = ResourceLocation.parse(type);
        for (ArgumentTypeParser parser : PARSERS) {
            if (parser.canParse(resourceLocation)) {
                @SuppressWarnings("unchecked")
                ArgumentType<T> parse = (ArgumentType<T>) parser.parse(context, resourceLocation, args);
                return parse;
            }
        }
        throw new IllegalArgumentException("Unknown argument type: " + type);
    }
}