package me.drex.meliuscommands.config.modifier.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.Optionull;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Objects;

public interface RequirementModifierType<T extends RequirementModifier> {
    Codec<RequirementModifierType<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(id ->
            Optionull.mapOrElse(RequirementModifiers.REQUIREMENTS.get(id), DataResult::success, () -> DataResult.error(() -> "Unknown matcher key: '"+ id + "'! Possible values: " + Arrays.toString(RequirementModifiers.REQUIREMENTS.keySet().toArray()))),
        RequirementModifierType::id
    );

    ResourceLocation id();

    MapCodec<T> codec();

    static <T extends RequirementModifier> RequirementModifierType<T> create(ResourceLocation id, MapCodec<T> codec) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(codec, "codec cannot be null");

        return new RequirementModifierType<>() {
            @Override
            public ResourceLocation id() {
                return id;
            }

            @Override
            public MapCodec<T> codec() {
                return codec;
            }
        };
    }
}
