package me.drex.meliuscommands.config.modifier.matcher.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifier;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifiers;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcher;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcherType;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatchers;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public record RegexCommandMatcher(List<String> regexes, List<ExecutionModifier> executionModifiers) implements CommandMatcher {
    public static final MapCodec<RegexCommandMatcher> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.withAlternative(Codec.STRING.listOf(), Codec.STRING, List::of).fieldOf("regexes").forGetter(RegexCommandMatcher::regexes),
            ExecutionModifiers.CODEC.listOf().fieldOf("execution_modifiers").forGetter(RegexCommandMatcher::executionModifiers)
        ).apply(instance, RegexCommandMatcher::new)
    );

    @Override
    public boolean matches(CommandContext<CommandSourceStack> context) {
        for (String regex : regexes) {
            if (context.getInput().matches(regex)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CommandMatcherType<?> getType() {
        return CommandMatchers.REGEX_COMMAND_MATCHER;
    }
}