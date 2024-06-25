package me.drex.meliuscommands.config.modifier.execution;

import com.mojang.serialization.Codec;
import me.drex.meliuscommands.config.modifier.execution.is_executable.AddPredicateModifier;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ExecutionModifiers {
    public static final Map<ResourceLocation, ExecutionModifierType<?>> COMMAND_MODIFIERS = new HashMap<>();

    public static final Codec<ExecutionModifier> CODEC = ExecutionModifierType.TYPE_CODEC.dispatch(ExecutionModifier::getType, ExecutionModifierType::codec);

    public static final ExecutionModifierType<AddPredicateModifier> ADD_PREDICATE = ExecutionModifierType.create(ResourceLocation.fromNamespaceAndPath("predicate", "add"), AddPredicateModifier.CODEC);
    // TODO CooldownModifier

    static {
        register(ADD_PREDICATE);
    }

    public static void register(ExecutionModifierType<?> executionModifier) {
        COMMAND_MODIFIERS.put(executionModifier.id(), executionModifier);
    }
}
