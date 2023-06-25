package me.drex.meliuscommands.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.tree.CommandNode;
import eu.pb4.predicate.api.GsonPredicateSerializer;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import me.drex.meliuscommands.CustomCommands;
import me.drex.meliuscommands.config.commands.LiteralNode;
import me.drex.meliuscommands.config.requirements.Requirement;
import me.drex.meliuscommands.mixin.CommandNodeAccessor;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static me.drex.meliuscommands.CustomCommands.LOGGER;

public class ConfigManager {

    private static final Path MAIN_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("melius-commands");
    public static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .setLenient()
        .registerTypeHierarchyAdapter(MinecraftPredicate.class, GsonPredicateSerializer.INSTANCE).create();

    public static final ResourceLocation MODIFY_COMMAND_REQUIREMENTS = new ResourceLocation("melius-commands", "modify_command_requirements");

    private static final Map<Path, LiteralNode> CUSTOM_COMMANDS = new HashMap<>();
    private static final Map<Path, Requirement> REQUIREMENT_MODIFICATIONS = new HashMap<>();

    public static void init() {
        CommandRegistrationCallback.EVENT.addPhaseOrdering(Event.DEFAULT_PHASE, MODIFY_COMMAND_REQUIREMENTS);
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            load();
            CUSTOM_COMMANDS.forEach((path, literalNode) -> {
                try {
                    dispatcher.register(literalNode.build(context));
                } catch (Exception exception) {
                    CustomCommands.LOGGER.error("Failed to register command {}", path.getFileName(), exception);
                }
            });
        });
        CommandRegistrationCallback.EVENT.register(MODIFY_COMMAND_REQUIREMENTS, (dispatcher, context, selection) -> {
            REQUIREMENT_MODIFICATIONS.forEach((path, requirementModification) -> {
                CommandNode<CommandSourceStack> node = dispatcher.findNode(List.of(requirementModification.commandPath.split("\\.")));
                if (node != null) {
                    Predicate<CommandSourceStack> originalRequirement = node.getRequirement();
                    Predicate<CommandSourceStack> requirementModificationPredicate = src -> requirementModification.require.test(PredicateContext.of(src)).success();
                    Predicate<CommandSourceStack> updatedRequirement;
                    if (requirementModification.replace) {
                        updatedRequirement = requirementModificationPredicate;
                    } else {
                        updatedRequirement = originalRequirement.and(requirementModificationPredicate);
                    }
                    //noinspection unchecked
                    ((CommandNodeAccessor<CommandSourceStack>) node).setRequirement(updatedRequirement);
                }
            });
        });
    }

    public static void load() {
        Path commandsPath = MAIN_FOLDER.resolve("commands");
        Path requirementsPath = MAIN_FOLDER.resolve("requirements");
        try {
            Files.createDirectories(MAIN_FOLDER);
            Files.createDirectories(commandsPath);
            Files.createDirectories(requirementsPath);
        } catch (IOException e) {
            LOGGER.error("Failed to create default folders", e);
        }
        CUSTOM_COMMANDS.clear();
        try (Stream<Path> commandsPaths = Files.walk(commandsPath, 1)) {
            commandsPaths.forEach(path -> {
                if (Files.isDirectory(path)) return;
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    LiteralNode literalNode = GSON.fromJson(reader, LiteralNode.class);
                    CUSTOM_COMMANDS.put(path, literalNode);
                } catch (IOException e) {
                    LOGGER.error("Failed to load custom command {}", path.getFileName(), e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to load custom commands", e);
        }
        LOGGER.info("Loaded {} custom commands", CUSTOM_COMMANDS.size());

        REQUIREMENT_MODIFICATIONS.clear();
        try (Stream<Path> requirementsPaths = Files.walk(requirementsPath, 1)) {
            requirementsPaths.forEach(path -> {
                if (Files.isDirectory(path)) return;
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    Requirement requirement = GSON.fromJson(reader, Requirement.class);
                    REQUIREMENT_MODIFICATIONS.put(path, requirement);
                } catch (IOException e) {
                    LOGGER.error("Failed to load command requirement {}", path.getFileName(), e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to load command requirements", e);
        }
        LOGGER.info("Loaded {} command requirements", REQUIREMENT_MODIFICATIONS.size());

    }


}
