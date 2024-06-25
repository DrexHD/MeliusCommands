/*
 * This file is part of commodore, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.drex.meliuscommands.parser;

import com.mojang.brigadier.arguments.ArgumentType;
import me.drex.meliuscommands.mixin.ArgumentTypesAccessor;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class MinecraftArgumentTypeParser implements ArgumentTypeParser {

    public static final MinecraftArgumentTypeParser INSTANCE = new MinecraftArgumentTypeParser();
    private static final Map<ResourceLocation, ArgumentParserFunction> ARGUMENT_TYPE_PARSERS = Map.of(
        ResourceLocation.withDefaultNamespace("entity"), MinecraftArgumentTypeParser::parseEntityArgumentType,
        ResourceLocation.withDefaultNamespace("resource"), MinecraftArgumentTypeParser::parseResourceArgumentType,
        ResourceLocation.withDefaultNamespace("resource_key"), MinecraftArgumentTypeParser::parseResourceKeyArgumentType,
        ResourceLocation.withDefaultNamespace("resource_or_tag"), MinecraftArgumentTypeParser::parseResourceOrTagArgumentType,
        ResourceLocation.withDefaultNamespace("resource_or_tag_key"), MinecraftArgumentTypeParser::parseResourceOrTagKeyArgumentType,
        ResourceLocation.withDefaultNamespace("score_holder"), MinecraftArgumentTypeParser::parseScoreHolderArgumentType,
        ResourceLocation.withDefaultNamespace("time"), MinecraftArgumentTypeParser::parseTimeArgumentType
    );

    private MinecraftArgumentTypeParser() {
    }

    @Override
    public boolean canParse(ResourceLocation resourceLocation) {
        return BuiltInRegistries.COMMAND_ARGUMENT_TYPE.get(resourceLocation) != null;
    }

    @Override
    public ArgumentType<?> parse(CommandBuildContext context, ResourceLocation resourceLocation, String args) {
        Class<? extends ArgumentType<?>> clazz = getClassByKey(resourceLocation);
        ArgumentParserFunction parserFunction = ARGUMENT_TYPE_PARSERS.get(resourceLocation);
        if (parserFunction != null) {
            return parserFunction.parse(clazz, context, args);
        }
        Constructor<? extends ArgumentType<?>> constructor;
        Object[] arguments;
        try {
            constructor = clazz.getDeclaredConstructor();
            arguments = new Object[]{};
        } catch (NoSuchMethodException noDefaultConstructor) {
            try {
                constructor = clazz.getDeclaredConstructor(CommandBuildContext.class);
                arguments = new Object[]{context};
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArgumentType<?> parseEntityArgumentType(Class<? extends ArgumentType<?>> clazz, CommandBuildContext context, String args) {
        boolean single;
        boolean playersOnly;
        switch (args) {
            case "entity" -> {
                single = true;
                playersOnly = false;
            }
            case "entities" -> {
                single = false;
                playersOnly = false;
            }
            case "player" -> {
                single = true;
                playersOnly = true;
            }
            case "players" -> {
                single = false;
                playersOnly = true;
            }
            default ->
                throw new IllegalArgumentException("Unknown entity selection type: \"" + args + "\". Use entity, entities, player or players.");
        }

        return constructMinecraftArgumentType(clazz, new Class[]{boolean.class, boolean.class}, single, playersOnly);
    }

    private static ArgumentType<?> parseResourceArgumentType(Class<? extends ArgumentType<?>> clazz, CommandBuildContext context, String args) {
        ResourceLocation resourceLocation = ResourceLocation.parse(args);
        return constructMinecraftArgumentType(clazz, new Class[]{CommandBuildContext.class, ResourceKey.class}, context, ResourceKey.createRegistryKey(resourceLocation));
    }

    private static ArgumentType<?> parseResourceKeyArgumentType(Class<? extends ArgumentType<?>> clazz, CommandBuildContext context, String args) {
        ResourceLocation resourceLocation = ResourceLocation.parse(args);
        return constructMinecraftArgumentType(clazz, new Class[]{ResourceKey.class}, ResourceKey.createRegistryKey(resourceLocation));
    }

    private static ArgumentType<?> parseResourceOrTagArgumentType(Class<? extends ArgumentType<?>> clazz, CommandBuildContext context, String args) {
        ResourceLocation resourceLocation = ResourceLocation.parse(args);
        return constructMinecraftArgumentType(clazz, new Class[]{CommandBuildContext.class, ResourceKey.class}, context, ResourceKey.createRegistryKey(resourceLocation));
    }

    private static ArgumentType<?> parseResourceOrTagKeyArgumentType(Class<? extends ArgumentType<?>> clazz, CommandBuildContext context, String args) {
        ResourceLocation resourceLocation = ResourceLocation.parse(args);
        return constructMinecraftArgumentType(clazz, new Class[]{ResourceKey.class}, ResourceKey.createRegistryKey(resourceLocation));
    }

    private static ArgumentType<?> parseScoreHolderArgumentType(Class<? extends ArgumentType<?>> clazz, CommandBuildContext context, String args) {
        boolean multiple = Boolean.parseBoolean(args);
        return constructMinecraftArgumentType(clazz, new Class[]{boolean.class}, multiple);
    }

    private static ArgumentType<?> parseTimeArgumentType(Class<? extends ArgumentType<?>> clazz, CommandBuildContext context, String args) {
        int minimum = Integer.parseInt(args);
        return constructMinecraftArgumentType(clazz, new Class[]{int.class}, minimum);
    }

    private static ArgumentType<?> constructMinecraftArgumentType(Class<? extends ArgumentType<?>> clazz, Class<?>[] argTypes, Object... args) {
        try {
            final Constructor<? extends ArgumentType<?>> constructor = clazz.getDeclaredConstructor(argTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a registered argument type class by resourceLocation.
     *
     * @param resourceLocation the resourceLocation
     * @return the returned argument type class
     * @throws IllegalArgumentException if no such argument is registered
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends ArgumentType<?>> getClassByKey(ResourceLocation resourceLocation) throws IllegalArgumentException {
        ArgumentTypeInfo<?, ?> entry = BuiltInRegistries.COMMAND_ARGUMENT_TYPE.get(resourceLocation);
        if (entry == null) {
            throw new IllegalArgumentException(resourceLocation.toString());
        }

        final Map<Class<?>, ArgumentTypeInfo<?, ?>> map = ArgumentTypesAccessor.getBY_CLASS();
        for (final Map.Entry<Class<?>, ArgumentTypeInfo<?, ?>> mapEntry : map.entrySet()) {
            if (mapEntry.getValue() == entry) {
                return (Class<? extends ArgumentType<?>>) mapEntry.getKey();
            }
        }
        throw new IllegalArgumentException(resourceLocation.toString());
    }

    @FunctionalInterface
    interface ArgumentParserFunction {
        ArgumentType<?> parse(Class<? extends ArgumentType<?>> clazz, CommandBuildContext context, String args);
    }

}
