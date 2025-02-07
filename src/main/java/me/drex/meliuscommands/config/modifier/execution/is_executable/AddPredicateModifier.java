package me.drex.meliuscommands.config.modifier.execution.is_executable;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.predicate.api.PredicateRegistry;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifierType;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifiers;
import me.drex.meliuscommands.config.common.CommandAction;
import me.drex.meliuscommands.util.CodecUtil;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public record AddPredicateModifier(MinecraftPredicate predicate, List<CommandAction> failure) implements IsExecutableModifier {
    public static final MapCodec<AddPredicateModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            PredicateRegistry.CODEC.fieldOf("predicate").forGetter(AddPredicateModifier::predicate),
            CodecUtil.withAlternative(CommandAction.CODEC.listOf(), CommandAction.CODEC, List::of).optionalFieldOf("failure", List.of()).forGetter(AddPredicateModifier::failure)
        ).apply(instance, AddPredicateModifier::new)
    );


    @Override
    public ExecutionModifierType<?> getType() {
        return ExecutionModifiers.ADD_PREDICATE;
    }

    @Override
    public boolean isExecutable(CommandContext<CommandSourceStack> context) {
        return predicate.test(PredicateContext.of(context.getSource())).success();
    }
}
