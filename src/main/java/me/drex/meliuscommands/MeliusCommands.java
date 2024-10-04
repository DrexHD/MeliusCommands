package me.drex.meliuscommands;

import me.drex.meliuscommands.commands.MeliusCommandsCommand;
import me.drex.meliuscommands.config.ConfigManager;
import me.drex.meliuscommands.mixin.CommandSourceStackAccessor;
import me.drex.meliuscommands.util.CooldownManager;
import me.drex.meliuscommands.util.PathCache;
import me.drex.meliuscommands.util.PlaceholderManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class MeliusCommands implements ModInitializer {

    public static final String MOD_ID = "melius-commands";
    public static final Logger LOGGER = LoggerFactory.getLogger("melius-commands");
    public static final Predicate<CommandSourceStack> IS_CONSOLE = source -> ((CommandSourceStackAccessor) source).getSource() == source.getServer();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            server.getPlayerList().getPlayers().forEach(player ->
                server.getCommands().sendCommands(player)
            );
            PathCache.invalidate();
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            MeliusCommandsCommand.register(dispatcher);
        });
        ConfigManager.init();
        CooldownManager.init();
        PlaceholderManager.init();
    }

}
