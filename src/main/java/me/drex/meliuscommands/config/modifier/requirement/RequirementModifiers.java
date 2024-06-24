package me.drex.meliuscommands.config.modifier.requirement;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RequirementModifiers {
    public static final Map<ResourceLocation, RequirementModifierType<?>> REQUIREMENTS = new HashMap<>();

    public static final Codec<RequirementModifier> CODEC = RequirementModifierType.TYPE_CODEC.dispatch(RequirementModifier::getType, RequirementModifierType::codec);

    public static final RequirementModifierType<AndRequirementModifier> REQUIREMENT_AND_MODIFIER = RequirementModifierType.create(new ResourceLocation("requirement", "and"), AndRequirementModifier.CODEC);
    public static final RequirementModifierType<OrRequirementModifier> REQUIREMENT_OR_MODIFIER = RequirementModifierType.create(new ResourceLocation("requirement", "or"), OrRequirementModifier.CODEC);
    public static final RequirementModifierType<ReplaceRequirementModifier> REQUIREMENT_REPLACE_MODIFIER = RequirementModifierType.create(new ResourceLocation("requirement", "replace"), ReplaceRequirementModifier.CODEC);


    static {
        register(REQUIREMENT_AND_MODIFIER);
        register(REQUIREMENT_OR_MODIFIER);
        register(REQUIREMENT_REPLACE_MODIFIER);
    }

    public static void register(RequirementModifierType<?> requirementModifier) {
        REQUIREMENTS.put(requirementModifier.id(), requirementModifier);
    }
}
