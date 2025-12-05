package me.drex.meliuscommands.config.modifier.execution;

import com.mojang.serialization.Codec;
import me.drex.meliuscommands.config.modifier.execution.is_executable.AddPredicateModifier;
import me.drex.meliuscommands.config.modifier.execution.is_executable.CooldownModifier;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ExecutionModifiers {
    public static final Map<Identifier, ExecutionModifierType<?>> COMMAND_MODIFIERS = new HashMap<>();

    public static final Codec<ExecutionModifier> CODEC = ExecutionModifierType.TYPE_CODEC.dispatch(ExecutionModifier::getType, ExecutionModifierType::codec);

    public static final ExecutionModifierType<AddPredicateModifier> ADD_PREDICATE = ExecutionModifierType.create(Identifier.fromNamespaceAndPath("predicate", "add"), AddPredicateModifier.CODEC);
    public static final ExecutionModifierType<CooldownModifier> COOLDOWN = ExecutionModifierType.create(Identifier.fromNamespaceAndPath("cooldown", "set"), CooldownModifier.CODEC);

    static {
        register(ADD_PREDICATE);
        register(COOLDOWN);
    }

    public static void register(ExecutionModifierType<?> executionModifier) {
        COMMAND_MODIFIERS.put(executionModifier.id(), executionModifier);
    }
}
