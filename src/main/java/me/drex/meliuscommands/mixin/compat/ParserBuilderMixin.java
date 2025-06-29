package me.drex.meliuscommands.mixin.compat;

import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.LinkedHashMap;

// Backport of https://github.com/Patbox/TextPlaceholderAPI/pull/67
//? if placeholder-api: < 2.6.0 {
/*@Mixin(ParserBuilder.class)
public abstract class ParserBuilderMixin {
    @Redirect(
        method = "<init>",
        at = @At(
            value = "NEW",
            target = "()Ljava/util/HashMap;"
        )
    )
    public HashMap<TagLikeParser.Format, TagLikeParser.Provider> useLinkedHashMap() {
        return new LinkedHashMap<>();
    }
}
*///?} else {
@Mixin(MinecraftServer.class)
public abstract class ParserBuilderMixin {

}
//?}