package me.drex.meliuscommands.config.modifier.matcher.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifier;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcherType;
import me.drex.meliuscommands.config.modifier.requirement.RequirementModifier;
import me.drex.meliuscommands.config.modifier.requirement.RequirementModifiers;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifiers;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatchers;
import me.drex.meliuscommands.util.CodecUtil;

import java.util.List;
import java.util.Optional;

public record StartsWithNodeMatcher(List<String> paths, Optional<RequirementModifier> requirementModifier, List<ExecutionModifier> executionModifiers) implements NodeMatcher {
    public static final MapCodec<StartsWithNodeMatcher> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            CodecUtil.withAlternative(Codec.STRING.listOf(), Codec.STRING, List::of).fieldOf("paths").forGetter(StartsWithNodeMatcher::paths),
            RequirementModifiers.CODEC.optionalFieldOf("requirement_modifier").forGetter(StartsWithNodeMatcher::requirementModifier),
            ExecutionModifiers.CODEC.listOf().optionalFieldOf("execution_modifiers", List.of()).forGetter(StartsWithNodeMatcher::executionModifiers)
        ).apply(instance, StartsWithNodeMatcher::new)
    );

    @Override
    public boolean matches(String path) {
        String[] parts = path.split("\\.");
        for (String lenientPath : paths) {
            String[] lenientParts = lenientPath.split("\\.");
            if (lenientParts.length > parts.length) continue;
            boolean matches = true;
            for (int i = 0; i < lenientParts.length; i++) {
                String lenientPart = lenientParts[i];
                if (!lenientPart.equals(parts[i])) {
                    matches = false;
                    break;
                }
            }
            if (matches) return true;
        }
        return false;
    }

    @Override
    public CommandMatcherType<?> getType() {
        return CommandMatchers.STARTS_WITH_NODE_MATCHER;
    }
}