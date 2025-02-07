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
import me.drex.meliuscommands.util.CodecUtil;

import java.util.List;
import java.util.Optional;

public record StrictNodeMatcher(List<String> paths, Optional<RequirementModifier> requirementModifier, List<ExecutionModifier> executionModifiers) implements NodeMatcher {
    public static final MapCodec<StrictNodeMatcher> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            CodecUtil.withAlternative(Codec.STRING.listOf(), Codec.STRING, List::of).fieldOf("paths").forGetter(StrictNodeMatcher::paths),
            RequirementModifiers.CODEC.optionalFieldOf("requirement_modifier").forGetter(StrictNodeMatcher::requirementModifier),
            ExecutionModifiers.CODEC.listOf().optionalFieldOf("execution_modifiers", List.of()).forGetter(StrictNodeMatcher::executionModifiers)
        ).apply(instance, StrictNodeMatcher::new)
    );

    @Override
    public boolean matches(String path) {
        return paths.contains(path);
    }

    @Override
    public CommandMatcherType<?> getType() {
        return CommandMatchers.STRICT_NODE_MATCHER;
    }
}