package me.drex.meliuscommands.mixin;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Component.Serializer.class)
public abstract class ComponentMixin {

    @ModifyConstant(method = "getPos", constant = @Constant(intValue = 1))
    private static int fixComponentArgument(int one) {
        return 0;
    }

}
