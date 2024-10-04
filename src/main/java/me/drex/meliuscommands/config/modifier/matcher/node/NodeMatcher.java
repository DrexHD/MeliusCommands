package me.drex.meliuscommands.config.modifier.matcher.node;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import me.drex.meliuscommands.config.modifier.requirement.RequirementModifier;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcher;
import me.drex.meliuscommands.util.PathCache;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;
import java.util.Optional;

public interface NodeMatcher extends CommandMatcher {
    Optional<RequirementModifier> requirementModifier();

    boolean matches(String path);

    @Override
    default boolean matches(CommandContext<CommandSourceStack> context) {
        CommandContext<CommandSourceStack> lastChild = context.getLastChild();
        CommandDispatcher<CommandSourceStack> dispatcher = context.getSource().getServer().getCommands().getDispatcher();
        List<ParsedCommandNode<CommandSourceStack>> nodes = lastChild.getNodes();
        assert !nodes.isEmpty();
        String path = PathCache.getPath(dispatcher, nodes.get(nodes.size() - 1).getNode());
        return matches(path);
    }
}
