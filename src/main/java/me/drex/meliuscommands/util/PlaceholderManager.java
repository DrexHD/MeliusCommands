package me.drex.meliuscommands.util;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.drex.meliuscommands.MeliusCommands;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class PlaceholderManager {

    public static void init() {
        Placeholders.register(Identifier.fromNamespaceAndPath(MeliusCommands.MOD_ID, "cooldown"), (context, argument) -> {
            ServerPlayer player = context.player();
            if (player == null) {
                return PlaceholderResult.invalid("No Player");
            }
            if (argument == null) {
                return PlaceholderResult.invalid("No Argument");
            }
            String[] parts = argument.split(" ", 2);

            String id = parts[0];
            String format = "HH:mm:ss";
            if (parts.length == 2) {
                format = parts[1];
            }
            long cooldown = CooldownManager.getCooldown(player.getUUID(), id);
            return PlaceholderResult.value(DurationFormatUtils.formatDuration(cooldown, format));
        });
    }

}
