package me.drex.meliuscommands.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.JsonOps;
import eu.pb4.predicate.api.PredicateRegistry;
import me.drex.meliuscommands.MeliusCommands;
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
    private static final Map<Path, LiteralNode> CUSTOM_COMMANDS = new HashMap<>();

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            load();
            CUSTOM_COMMANDS.forEach((path, literalNode) -> {
                try {
                    dispatcher.register(literalNode.build(context));
                } catch (Exception exception) {
                    MeliusCommands.LOGGER.error("Failed to register command {}", path.getFileName(), exception);
                }
            });
        });
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
        try (Stream<Path> commandsPaths = Files.walk(commandsPath, 1)) {
            commandsPaths.forEach(path -> {
                if (Files.isDirectory(path)) return;
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    JsonReader jsonReader = new JsonReader(reader);
                    jsonReader.setLenient(false);
                    JsonElement jsonElement = Streams.parse(jsonReader);
                    LiteralNode literalNode = LiteralNode.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
                    CUSTOM_COMMANDS.put(path, literalNode);
                } catch (IOException e) {
                    LOGGER.error("Couldn't access custom command in {}", path.getFileName(), e);
                } catch (JsonParseException e) {
                    LOGGER.error("Couldn't to parse custom command in {}", path.getFileName(), e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to load custom commands", e);
        }
        LOGGER.info("Loaded {} custom command", CUSTOM_COMMANDS.size());

        COMMAND_EXECUTION_MATCHERS.clear();
        try (Stream<Path> requirementsPaths = Files.walk(modifiersPath, 1)) {
            requirementsPaths.forEach(path -> {
                if (Files.isDirectory(path)) return;
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    JsonReader jsonReader = new JsonReader(reader);
                    jsonReader.setLenient(false);
                    JsonElement jsonElement = Streams.parse(jsonReader);
                    CommandMatcher matcher = CommandMatchers.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
                    COMMAND_EXECUTION_MATCHERS.add(matcher);
                } catch (IOException e) {
                    LOGGER.error("Couldn't access command modifier in {}", path.getFileName(), e);
                } catch (JsonParseException e) {
                    LOGGER.error("Couldn't to parse command modifier in {}", path.getFileName(), e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to load command requirements", e);
        }
        LOGGER.info("Loaded {} command modifiers", COMMAND_EXECUTION_MATCHERS.size());
    }

}
