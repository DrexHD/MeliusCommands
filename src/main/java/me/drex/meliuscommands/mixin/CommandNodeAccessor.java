package me.drex.meliuscommands.mixin;

import com.mojang.brigadier.tree.CommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;

@Mixin(CommandNode.class)
public interface CommandNodeAccessor<S> {

    @Accessor(remap = false)
    @Mutable
    void setRequirement(Predicate<S> requirement);

}
