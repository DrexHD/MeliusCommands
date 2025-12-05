package me.drex.meliuscommands.config.modifier.execution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.Optionull;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.Objects;

public interface ExecutionModifierType<T extends ExecutionModifier> {
    Codec<ExecutionModifierType<?>> TYPE_CODEC = Identifier.CODEC.comapFlatMap(id ->
            Optionull.mapOrElse(ExecutionModifiers.COMMAND_MODIFIERS.get(id), DataResult::success, () -> DataResult.error(() -> "Unknown matcher key: '"+ id + "'! Possible values: " + Arrays.toString(ExecutionModifiers.COMMAND_MODIFIERS.keySet().toArray()))),
        ExecutionModifierType::id
    );

    Identifier id();

    MapCodec<T> codec();

    static <T extends ExecutionModifier> ExecutionModifierType<T> create(Identifier id, MapCodec<T> codec) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(codec, "codec cannot be null");

        return new ExecutionModifierType<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public MapCodec<T> codec() {
                return codec;
            }
        };
    }
}
