package me.drex.meliuscommands.util;

import me.drex.meliuscommands.MeliusCommands;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CommandDelayManager {

    private static final List<DelayedCommand> delayedCommands = new ArrayList<>();

    private CommandDelayManager() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> tick());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> delayedCommands.clear());
    }

    public static void schedule(int delayTicks, Runnable command) {
        if (delayTicks <= 0) {
            command.run();
            return;
        }
        delayedCommands.add(new DelayedCommand(delayTicks, command));
    }

    private static void tick() {
        if (delayedCommands.isEmpty()) {
            return;
        }

        List<Runnable> readyCommands = new ArrayList<>();
        Iterator<DelayedCommand> iterator = delayedCommands.iterator();
        while (iterator.hasNext()) {
            DelayedCommand delayedCommand = iterator.next();
            delayedCommand.remainingTicks--;
            if (delayedCommand.remainingTicks <= 0) {
                readyCommands.add(delayedCommand.command);
                iterator.remove();
            }
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
