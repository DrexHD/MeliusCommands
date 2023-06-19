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
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;

public class MinecraftArgumentTypeParser implements ArgumentTypeParser {

    public static final MinecraftArgumentTypeParser INSTANCE = new MinecraftArgumentTypeParser();

    private MinecraftArgumentTypeParser() {
    }

    @Override
    public boolean canParse(String namespace, String name) {
        if (namespace.equals("minecraft") && (name.equals("entity") || name.equals("score_holder"))) {
            return true;
        }

        return isRegistered(new ResourceLocation(namespace, name));
    }

    @Override
    public ArgumentType<?> parse(CommandBuildContext context, String namespace, String name, String args) {
        if (namespace.equals("minecraft")) {
            if (name.equals("entity")) {
                return parseEntityArgumentType(args);
            }
            if (name.equals("score_holder")) {
                return parseScoreHolderArgumentType(args);
            }
        }

        try {
            return getByKey(context, new ResourceLocation(namespace, name));
        } catch (Throwable e) {
            throw new IllegalArgumentException("Invalid key for argument type (not found in registry): " + namespace + ":" + name, e);
        }
    }

    private static ArgumentType<?> parseScoreHolderArgumentType(String args) {
        boolean multiple = Boolean.parseBoolean(args);
        return constructMinecraftArgumentType(new ResourceLocation("score_holder"), new Class[]{boolean.class}, multiple);
    }

    private ArgumentType<?> parseEntityArgumentType(String args) {

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
            default -> throw new IllegalArgumentException("Unknown entity selection type: \"" + args + "\". Use entity, entities, player or players.");
        }

        return constructMinecraftArgumentType(new ResourceLocation("entity"), new Class[]{boolean.class, boolean.class}, single, playersOnly);
    }

    private static ArgumentType<?> constructMinecraftArgumentType(ResourceLocation key, Class<?>[] argTypes, Object... args) {
        try {
            final Constructor<? extends ArgumentType<?>> constructor = getClassByKey(key).getDeclaredConstructor(argTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets if an argument is registered for the given resourceLocation.
     *
     * @param resourceLocation the resourceLocation
     * @return if an argument is registered
     */
    public static boolean isRegistered(ResourceLocation resourceLocation) {
        return BuiltInRegistries.COMMAND_ARGUMENT_TYPE.get(resourceLocation) != null;
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

    /**
     * Gets a registered argument type by key.
     *
     * @param resourceLocation the key
     * @return the returned argument
     * @throws IllegalArgumentException if no such argument is registered
     */
    public static ArgumentType<?> getByKey(CommandBuildContext context, ResourceLocation resourceLocation) throws IllegalArgumentException {
        try {
            Class<? extends ArgumentType<?>> classByKey = getClassByKey(resourceLocation);
            for (Constructor<? > constructor : classByKey.getDeclaredConstructors()) {
                constructor.setAccessible(true);
                if (constructor.getParameterCount() == 0) return (ArgumentType<?>) constructor.newInstance();
                return (ArgumentType<?>) constructor.newInstance(context);
            }
            throw new RuntimeException("Couldn't find appropriate constructor");
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
