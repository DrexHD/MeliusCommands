package me.drex.meliuscommands.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import me.drex.meliuscommands.MeliusCommands;
import me.drex.meliuscommands.commands.MeliusCommandsCommand;
import me.drex.meliuscommands.config.ConfigManager;
import me.drex.meliuscommands.config.command.LiteralNode;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcher;
import me.drex.meliuscommands.config.modifier.matcher.node.NodeMatcher;
import me.drex.meliuscommands.config.modifier.requirement.RequirementModifier;
import me.drex.meliuscommands.util.PathCache;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

import static me.drex.meliuscommands.MeliusCommands.IS_CONSOLE;

@Mixin(value = Commands.class, priority = 1500)
public class CommandsMixin {

    @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onRegister(Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext, CallbackInfo ci) {

        ConfigManager.CUSTOM_COMMANDS.forEach((path, literalNodes) -> {
            try {
                // Build all nodes in path
                List<LiteralArgumentBuilder<CommandSourceStack>> buildNodes = literalNodes.stream()
                    .map(literalNode -> literalNode.build(dispatcher, commandBuildContext))
                    .toList();

                for (LiteralNode literalNode : literalNodes) {
                    literalNode.build(dispatcher, commandBuildContext);
                }
                // Register nodes if all nodes were build successfully
                for (LiteralArgumentBuilder<CommandSourceStack> buildNode : buildNodes) {
                    dispatcher.register(buildNode);
                }
            } catch (Exception exception) {
                MeliusCommands.LOGGER.error("Failed to register commands {}", path.getFileName(), exception);
            }
        });

        // We are using RETURN with a high priority to ensure that we are *LAST* and can also modify commands which
        // don't use Fabric API
        for (CommandMatcher commandMatcher : ConfigManager.COMMAND_EXECUTION_MATCHERS) {
            if (commandMatcher instanceof NodeMatcher nodeMatcher && nodeMatcher.requirementModifier().isPresent()) {
                melius_commands$modifyCommandNode(nodeMatcher, nodeMatcher.requirementModifier().get(), dispatcher.getRoot());
            }
        }
    }

    @Redirect(
        method = "performCommand",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;isDebugEnabled()Z"
        )
    )
    public boolean redirectIsDebugEnabled(Logger instance) {
        return MeliusCommandsCommand.DEBUG_COMMAND_EXCEPTIONS;
    }

    @Unique
    private void melius_commands$modifyCommandNode(NodeMatcher nodeMatcher, RequirementModifier requirementModifier, CommandNode<CommandSourceStack> node) {
        String path = PathCache.getPath(dispatcher, node);
        if (nodeMatcher.matches(path)) {
            Predicate<CommandSourceStack> originalRequirement = node.getRequirement();
            //noinspection unchecked
            ((CommandNodeAccessor<CommandSourceStack>) node).setRequirement(
                requirementModifier.apply(originalRequirement)
                    .or(IS_CONSOLE)); // Console should always be able to execute commands
        }
        for (CommandNode<CommandSourceStack> child : node.getChildren()) {
            melius_commands$modifyCommandNode(nodeMatcher, requirementModifier, child);
        }
    }
}
