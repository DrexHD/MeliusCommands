package me.drex.meliuscommands.config.modifier.matcher;

import com.mojang.serialization.Codec;
import me.drex.meliuscommands.config.modifier.matcher.command.StartsWithCommandMatcher;
import me.drex.meliuscommands.config.modifier.matcher.command.StrictCommandMatcher;
import me.drex.meliuscommands.config.modifier.matcher.node.StartsWithNodeMatcher;
import me.drex.meliuscommands.config.modifier.matcher.command.RegexCommandMatcher;
import me.drex.meliuscommands.config.modifier.matcher.node.RegexNodeMatcher;
import me.drex.meliuscommands.config.modifier.matcher.node.StrictNodeMatcher;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CommandMatchers {
    public static final Map<Identifier, CommandMatcherType<?>> MATCHERS = new HashMap<>();

    public static final Codec<CommandMatcher> CODEC = CommandMatcherType.TYPE_CODEC.dispatch(CommandMatcher::getType, CommandMatcherType::codec);

    public static final CommandMatcherType<StrictCommandMatcher> STRICT_COMMAND_MATCHER = CommandMatcherType.create(Identifier.fromNamespaceAndPath("command", "strict"), StrictCommandMatcher.CODEC);
    public static final CommandMatcherType<StartsWithCommandMatcher> STARTS_WITH_COMMAND_MATCHER = CommandMatcherType.create(Identifier.fromNamespaceAndPath("command", "starts_with"), StartsWithCommandMatcher.CODEC);
    public static final CommandMatcherType<RegexCommandMatcher> REGEX_COMMAND_MATCHER = CommandMatcherType.create(Identifier.fromNamespaceAndPath("command", "regex"), RegexCommandMatcher.CODEC);
    public static final CommandMatcherType<StrictNodeMatcher> STRICT_NODE_MATCHER = CommandMatcherType.create(Identifier.fromNamespaceAndPath("node", "strict"), StrictNodeMatcher.CODEC);
    public static final CommandMatcherType<StartsWithNodeMatcher> STARTS_WITH_NODE_MATCHER = CommandMatcherType.create(Identifier.fromNamespaceAndPath("node", "starts_with"), StartsWithNodeMatcher.CODEC);
    public static final CommandMatcherType<RegexNodeMatcher> REGEX_NODE_MATCHER = CommandMatcherType.create(Identifier.fromNamespaceAndPath("node", "regex"), RegexNodeMatcher.CODEC);

    static {
        register(STRICT_COMMAND_MATCHER);
        register(STARTS_WITH_COMMAND_MATCHER);
        register(REGEX_COMMAND_MATCHER);

        register(STRICT_NODE_MATCHER);
        register(STARTS_WITH_NODE_MATCHER);
        register(REGEX_NODE_MATCHER);
    }

    public static void register(CommandMatcherType<?> commandMatcher) {
        MATCHERS.put(commandMatcher.id(), commandMatcher);
    }
}
