package me.drex.meliuscommands.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import me.drex.meliuscommands.config.command.LiteralNode;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatcher;
import me.drex.meliuscommands.config.modifier.matcher.CommandMatchers;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static me.drex.meliuscommands.MeliusCommands.LOGGER;

public class ConfigManager {

    private static final Path MAIN_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("melius-commands");

    public static final List<CommandMatcher> COMMAND_EXECUTION_MATCHERS = new LinkedList<>();
    public static final Map<Path, List<LiteralNode>> CUSTOM_COMMANDS = new HashMap<>();
    public static final Codec<List<LiteralNode>> COMMANDS_CODEC = Codec.withAlternative(Codec.list(LiteralNode.CODEC), LiteralNode.CODEC, List::of);
    public static final Codec<List<CommandMatcher>> MATCHERS_CODEC = Codec.withAlternative(Codec.list(CommandMatchers.CODEC), CommandMatchers.CODEC, List::of);

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> load());
    }

    public static void load() {
        Path commandsPath = MAIN_FOLDER.resolve("commands");
        Path modifiersPath = MAIN_FOLDER.resolve("modifiers");
        try {
            Files.createDirectories(commandsPath);
            Files.createDirectories(modifiersPath);
        } catch (IOException e) {
            LOGGER.error("Failed to create default folders", e);
        }
        CUSTOM_COMMANDS.clear();
        try (Stream<Path> commandPaths = Files.walk(commandsPath)) {
            commandPaths.forEach(path -> {
                if (Files.isDirectory(path)) return;
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    JsonReader jsonReader = new JsonReader(reader);
                    jsonReader.setLenient(false);
                    JsonElement jsonElement = Streams.parse(jsonReader);
                    List<LiteralNode> literalNodes = COMMANDS_CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
                    CUSTOM_COMMANDS.put(path, literalNodes);
                } catch (IOException e) {
                    LOGGER.error("Couldn't access custom commands in {}", path.getFileName(), e);
                } catch (JsonParseException e) {
                    LOGGER.error("Couldn't to parse custom commands in {}", path.getFileName(), e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to load custom commands", e);
        }
        LOGGER.info("Loaded {} custom command", CUSTOM_COMMANDS.size());

        COMMAND_EXECUTION_MATCHERS.clear();
        try (Stream<Path> modifierPaths = Files.walk(modifiersPath)) {
            modifierPaths.forEach(path -> {
                if (Files.isDirectory(path)) return;
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    JsonReader jsonReader = new JsonReader(reader);
                    jsonReader.setLenient(false);
                    JsonElement jsonElement = Streams.parse(jsonReader);
                    List<CommandMatcher> matchers = MATCHERS_CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
                    COMMAND_EXECUTION_MATCHERS.addAll(matchers);
                } catch (IOException e) {
                    LOGGER.error("Couldn't access command modifiers in {}", path.getFileName(), e);
                } catch (JsonParseException e) {
                    LOGGER.error("Couldn't to parse command modifiers in {}", path.getFileName(), e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to load command modifiers", e);
        }
        LOGGER.info("Loaded {} command modifiers", COMMAND_EXECUTION_MATCHERS.size());
    }

}
