package me.drex.meliuscommands.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import me.drex.meliuscommands.MeliusCommands;
import me.drex.meliuscommands.config.ConfigManager;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcher;
import me.drex.meliuscommands.config.modifier.matcher.node.NodeMatcher;
import me.drex.meliuscommands.config.modifier.requirement.RequirementModifier;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(value = Commands.class, priority = 1500)
public class CommandsMixin {

    @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onRegister(Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext, CallbackInfo ci) {
        ConfigManager.CUSTOM_COMMANDS.forEach((path, literalNode) -> {
            try {
                dispatcher.register(literalNode.build(dispatcher, commandBuildContext));
            } catch (Exception exception) {
                MeliusCommands.LOGGER.error("Failed to register command {}", path.getFileName(), exception);
            }
        });

        // We are using RETURN with a high priority to ensure that we are *LAST* and can also modify commands which
        // don't use Fabric API
        for (CommandMatcher commandMatcher : ConfigManager.COMMAND_EXECUTION_MATCHERS) {
            if (commandMatcher instanceof NodeMatcher nodeMatcher && nodeMatcher.requirementModifier().isPresent()) {
                melius_commands$modifyCommandNode(nodeMatcher.requirementModifier().get(), dispatcher.getRoot());
            }
        }
    }

    @Unique
    private static void melius_commands$modifyCommandNode(RequirementModifier requirementModifier, CommandNode<CommandSourceStack> node) {
        Predicate<CommandSourceStack> originalRequirement = node.getRequirement();
        //noinspection unchecked
        ((CommandNodeAccessor<CommandSourceStack>) node).setRequirement(
            requirementModifier.apply(originalRequirement)
                .or(source -> ((CommandSourceStackAccessor)source).getSource() == source.getServer())); // Console should always be able to execute commands
        for (CommandNode<CommandSourceStack> child : node.getChildren()) {
            melius_commands$modifyCommandNode(requirementModifier, child);
        }
    }
}
