package me.drex.meliuscommands;

import me.drex.meliuscommands.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomCommands implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("melius-commands");

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> server.getPlayerList().getPlayers().forEach(player -> server.getCommands().sendCommands(player)));
        ConfigManager.init();
    }


}
