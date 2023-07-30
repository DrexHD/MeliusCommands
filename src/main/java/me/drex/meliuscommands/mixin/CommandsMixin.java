package me.drex.meliuscommands.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import eu.pb4.predicate.api.PredicateContext;
import me.drex.meliuscommands.config.ConfigManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = Commands.class, priority = 1500)
public class CommandsMixin {

    @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onRegister(Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext, CallbackInfo ci) {
        // We are using RETURN with a high priority to ensure that we are *LAST* and can also modify commands which
        // don't use Fabric API
        ConfigManager.REQUIREMENT_MODIFICATIONS.forEach((path, requirementModification) -> {
            CommandNode<CommandSourceStack> node = dispatcher.findNode(List.of(requirementModification.commandPath.split("\\.")));
            if (node != null) {
                Predicate<CommandSourceStack> originalRequirement = node.getRequirement();
                Predicate<CommandSourceStack> requirementModificationPredicate = src -> requirementModification.require.test(PredicateContext.of(src)).success();
                Predicate<CommandSourceStack> updatedRequirement;
                if (requirementModification.replace) {
                    updatedRequirement = requirementModificationPredicate;
                } else {
                    updatedRequirement = originalRequirement.and(requirementModificationPredicate);
                }
                //noinspection unchecked
                ((CommandNodeAccessor<CommandSourceStack>) node).setRequirement(updatedRequirement);
            }
        });
    }

}
