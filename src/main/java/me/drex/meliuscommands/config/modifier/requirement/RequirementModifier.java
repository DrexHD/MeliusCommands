package me.drex.meliuscommands.config.modifier.requirement;

import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public interface RequirementModifier {
    RequirementModifierType<?> getType();

    Predicate<CommandSourceStack> apply(Predicate<CommandSourceStack> previous);
}
