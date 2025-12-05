package me.drex.meliuscommandstest.mixin;

import me.drex.meliuscommands.parser.MinecraftArgumentTypeParser;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MinecraftArgumentTypeParser.class)
public interface MinecraftArgumentTypeParserAccessor {

    @Accessor
    Map<Identifier, Object> getARGUMENT_TYPE_PARSERS();

}
