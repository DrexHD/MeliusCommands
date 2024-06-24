package me.drex.meliuscommands.config.modifier.requirement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.predicate.api.PredicateRegistry;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public record AndRequirementModifier(MinecraftPredicate predicate) implements RequirementModifier {
    public static final MapCodec<AndRequirementModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            PredicateRegistry.CODEC.fieldOf("predicate").forGetter(AndRequirementModifier::predicate)
        ).apply(instance, AndRequirementModifier::new)
    );

    @Override
    public RequirementModifierType<?> getType() {
        return RequirementModifiers.REQUIREMENT_AND_MODIFIER;
    }

    @Override
    public Predicate<CommandSourceStack> apply(Predicate<CommandSourceStack> previous) {
        return previous.and(source -> predicate.test(PredicateContext.of(source)).success());
    }
}
