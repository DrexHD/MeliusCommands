package me.drex.meliuscommands.config.modifier.execution.is_executable;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.meliuscommands.config.common.CommandAction;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifierType;
import me.drex.meliuscommands.config.modifier.execution.ExecutionModifiers;
import me.drex.meliuscommands.util.CodecUtil;
import me.drex.meliuscommands.util.CooldownManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public record CooldownModifier(String id, long seconds, List<CommandAction> failure) implements IsExecutableModifier {
    public static final MapCodec<CooldownModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(CooldownModifier::id),
            Codec.LONG.fieldOf("seconds").forGetter(CooldownModifier::seconds),
            CodecUtil.withAlternative(CommandAction.CODEC.listOf(), CommandAction.CODEC, List::of).optionalFieldOf("failure", List.of()).forGetter(CooldownModifier::failure)
        ).apply(instance, CooldownModifier::new)
    );

    @Override
    public ExecutionModifierType<?> getType() {
        return ExecutionModifiers.COOLDOWN;
    }

    @Override
    public boolean isExecutable(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            return true;
        }
        return !CooldownManager.hasCooldown(player.getUUID(), id);
    }

    @Override
    public void onSuccess(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            return;
        }
        CooldownManager.addCooldown(player.getUUID(), id, seconds);
    }
}
