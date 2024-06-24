package me.drex.meliuscommands.config.modifier.matcher.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifier;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifiers;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcherType;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatchers;
import me.drex.meliuscommands.config.modifier.requirement.RequirementModifier;
import me.drex.meliuscommands.config.modifier.requirement.RequirementModifiers;

import java.util.List;
import java.util.Optional;

public record RegexNodeMatcher(List<String> regexes, Optional<RequirementModifier> requirementModifier, List<ExecutionModifier> executionModifiers) implements NodeMatcher {
    public static final MapCodec<RegexNodeMatcher> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.withAlternative(Codec.STRING.listOf(), Codec.STRING, List::of).fieldOf("regexes").forGetter(RegexNodeMatcher::regexes),
            RequirementModifiers.CODEC.optionalFieldOf("requirement_modifier").forGetter(RegexNodeMatcher::requirementModifier),
            ExecutionModifiers.CODEC.listOf().optionalFieldOf("execution_modifiers", List.of()).forGetter(RegexNodeMatcher::executionModifiers)
        ).apply(instance, RegexNodeMatcher::new)
    );

    @Override
    public boolean matches(String path) {
        for (String regex : regexes) {
            if (path.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CommandMatcherType<?> getType() {
        return CommandMatchers.REGEX_NODE_MATCHER;
    }
}