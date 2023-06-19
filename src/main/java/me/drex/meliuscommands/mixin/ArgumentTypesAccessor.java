package me.drex.meliuscommands.mixin;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;


@Mixin(ArgumentTypeInfos.class)
public interface ArgumentTypesAccessor {

    @Accessor
    static Map<Class<?>, ArgumentTypeInfo<?, ?>> getBY_CLASS() {
        throw new AssertionError();
    }

}
