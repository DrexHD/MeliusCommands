package me.drex.meliuscommands.util;

import me.drex.meliuscommands.MeliusCommands;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class CommandDelayManager {

    private static final Map<MinecraftServer, List<DelayedCommand>> delayedCommands = new HashMap<>();

    private CommandDelayManager() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(CommandDelayManager::tick);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> delayedCommands.remove(server));
    }

    public static void schedule(MinecraftServer server, int delayTicks, Runnable command) {
        if (delayTicks <= 0) {
            command.run();
            return;
        }
        delayedCommands.computeIfAbsent(server, ignored -> new ArrayList<>()).add(new DelayedCommand(delayTicks, command));
    }

    private static void tick(MinecraftServer server) {
        List<DelayedCommand> serverDelayedCommands = delayedCommands.get(server);
        if (serverDelayedCommands == null || serverDelayedCommands.isEmpty()) {
            return;
        }

        List<Runnable> readyCommands = new ArrayList<>();
        Iterator<DelayedCommand> iterator = serverDelayedCommands.iterator();
        while (iterator.hasNext()) {
            DelayedCommand delayedCommand = iterator.next();
            delayedCommand.remainingTicks--;
            if (delayedCommand.remainingTicks <= 0) {
                readyCommands.add(delayedCommand.command);
                iterator.remove();
            }
        }

        if (serverDelayedCommands.isEmpty()) {
            delayedCommands.remove(server);
        }

        for (Runnable command : readyCommands) {
            try {
                command.run();
            } catch (Throwable throwable) {
                MeliusCommands.LOGGER.error("Failed to execute delayed command", throwable);
            }
        }
    }

    private static final class DelayedCommand {
        private int remainingTicks;
        private final Runnable command;

        private DelayedCommand(int remainingTicks, Runnable command) {
            this.remainingTicks = remainingTicks;
            this.command = command;
        }
    }
}