package me.drex.meliuscommands.config.modifier.matcher.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifier;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcherType;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifiers;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcher;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatchers;
import me.drex.meliuscommands.util.CodecUtil;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public record StrictCommandMatcher(List<String> commands, List<ExecutionModifier> executionModifiers) implements CommandMatcher {
    public static final MapCodec<StrictCommandMatcher> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            CodecUtil.withAlternative(Codec.STRING.listOf(), Codec.STRING, List::of).fieldOf("commands").forGetter(StrictCommandMatcher::commands),
            ExecutionModifiers.CODEC.listOf().fieldOf("execution_modifiers").forGetter(StrictCommandMatcher::executionModifiers)
        ).apply(instance, StrictCommandMatcher::new)
    );

    @Override
    public boolean matches(CommandContext<CommandSourceStack> context) {
        return commands.contains(context.getInput());
    }

    @Override
    public CommandMatcherType<?> getType() {
        return CommandMatchers.STRICT_COMMAND_MATCHER;
    }
}