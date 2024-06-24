package me.drex.meliuscommands.config.modifier.matcher.node;

import com.mojang.brigadier.context.CommandContext;
import me.drex.meliuscommands.config.modifier.requirement.RequirementModifier;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcher;
import net.minecraft.commands.CommandSourceStack;

import java.util.Optional;

public interface NodeMatcher extends CommandMatcher {
    Optional<RequirementModifier> requirementModifier();

    boolean matches(String path);

    @Override
    default boolean matches(CommandContext<CommandSourceStack> context) {
        String path = String.join(".", context.getNodes().stream().map(parsedNode -> parsedNode.getNode().getName()).toList());
        return matches(path);
    }
}
