package me.drex.meliuscommands.config.modifier.matcher;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.Optionull;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.Objects;

public interface CommandMatcherType<T extends CommandMatcher> {
    Codec<CommandMatcherType<?>> TYPE_CODEC = Identifier.CODEC.comapFlatMap(id ->
            Optionull.mapOrElse(CommandMatchers.MATCHERS.get(id), DataResult::success, () -> DataResult.error(() -> "Unknown matcher key: '"+ id + "'! Possible values: " + Arrays.toString(CommandMatchers.MATCHERS.keySet().toArray()))),
        CommandMatcherType::id
    );


    Identifier id();

    MapCodec<T> codec();

    static <T extends CommandMatcher> CommandMatcherType<T> create(Identifier id, MapCodec<T> codec) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(codec, "codec cannot be null");

        return new CommandMatcherType<>() {
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
