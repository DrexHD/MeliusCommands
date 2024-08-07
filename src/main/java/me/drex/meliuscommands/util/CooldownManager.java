package me.drex.meliuscommands.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.*;

public class CooldownManager {

    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public static void init() {
        ServerTickEvents.START_SERVER_TICK.register(server -> tick());
    }

    public static void addCooldown(UUID uuid, String id, long seconds) {
        long currentMillis = System.currentTimeMillis();
        long millis = seconds * 1000;
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(uuid, ignored -> new HashMap<>());
        playerCooldowns.put(id, currentMillis + millis);
    }

    private static void tick() {
        long currentMillis = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, Map<String, Long>>> iterator = cooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Map<String, Long>> mapEntry = iterator.next();
            mapEntry.getValue().entrySet().removeIf(cooldown -> cooldown.getValue() <= currentMillis);
            if (mapEntry.getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }

    public static boolean hasCooldown(UUID uuid, String id) {
        return getCooldown(uuid, id) > 0;
    }

    public static long getCooldown(UUID uuid, String id) {
        Map<String, Long> cooldown = CooldownManager.cooldowns.getOrDefault(uuid, Collections.emptyMap());
        return cooldown.getOrDefault(id, 0L) - System.currentTimeMillis();
    }

}
