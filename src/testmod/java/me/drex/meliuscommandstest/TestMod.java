package me.drex.meliuscommandstest;

import me.drex.meliuscommands.parser.BrigadierArgumentTypeParser;
import me.drex.meliuscommands.parser.MinecraftArgumentTypeParser;
import me.drex.meliuscommandstest.mixin.MinecraftArgumentTypeParserAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class TestMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("melius-commands-test");

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CommandBuildContext context = Commands.createValidationContext(VanillaRegistries.createLookup());

            BuiltInRegistries.COMMAND_ARGUMENT_TYPE.entrySet().forEach(entry -> {
                ResourceKey<ArgumentTypeInfo<?, ?>> key = entry.getKey();
                var resourceLocation = key.location();
                try {
                    var parser = MinecraftArgumentTypeParser.INSTANCE;
                    // These arguments already have special handling (that can still require changes, but this can't be
                    // tested automatically very easily)
                    Set<ResourceLocation> specialArguments = ((MinecraftArgumentTypeParserAccessor) parser).getARGUMENT_TYPE_PARSERS().keySet();
                    if (specialArguments.contains(resourceLocation)) {
                        return;
                    }
                    if (BrigadierArgumentTypeParser.INSTANCE.canParse(resourceLocation)) {
                        return;
                    }
                    if (parser.canParse(resourceLocation)) {
                        parser.parse(context, resourceLocation, "");
                        return;
                    }
                    LOGGER.warn("Unknown argument type: {}", resourceLocation);
                } catch (Exception e) {
                    LOGGER.error("Failed to parse: {}", resourceLocation, e);
                }
            });
        });
    }
}
