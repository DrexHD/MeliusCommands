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

import com.mojang.brigadier.arguments.*;
import net.minecraft.commands.CommandBuildContext;

import java.util.Arrays;

public class BrigadierArgumentTypeParser implements ArgumentTypeParser {
    public static final BrigadierArgumentTypeParser INSTANCE = new BrigadierArgumentTypeParser();

    private BrigadierArgumentTypeParser() {

    }

    @Override
    public boolean canParse(String namespace, String name) {
        if (!namespace.equals("brigadier")) {
            return false;
        }

        return switch (name) {
            case "bool", "string", "integer", "long", "float", "double" -> true;
            default -> false;
        };
    }

    @Override
    public ArgumentType<?> parse(CommandBuildContext context, String namespace, String name, String args) {
        return switch (name) {
            case "bool" -> BoolArgumentType.bool();
            case "string" -> parseStringArgumentType(args);
            case "integer" -> parseIntegerArgumentType(args);
            case "double" -> parseDoubleArgumentType(args);
            case "long" -> parseLongArgumentType(args);
            case "float" -> parseFloatArgumentType(args);
            default -> throw new AssertionError();
        };
    }

    private static StringArgumentType parseStringArgumentType(String args) {

        return switch (args) {
            case "single_word" -> StringArgumentType.word();
            case "quotable_phrase" -> StringArgumentType.string();
            case "greedy_phrase" -> StringArgumentType.greedyString();
            default -> throw new IllegalArgumentException("Unknown string type: " + args);
        };
    }

    private static IntegerArgumentType parseIntegerArgumentType(String args) {
        String[] arguments = args.split(" ");
        if (arguments.length > 2) {
            throw new IllegalArgumentException("Expected 0-2 arguments, but received " + arguments.length + ": " + Arrays.toString(arguments));
        } else {
            if (arguments.length > 0) {
                int min = Integer.parseInt(arguments[0]);
                if (arguments.length > 1) {
                    int max = Integer.parseInt(arguments[1]);
                    return IntegerArgumentType.integer(min, max);
                }
                return IntegerArgumentType.integer(min);
            }
            return IntegerArgumentType.integer();
        }
    }

    private static DoubleArgumentType parseDoubleArgumentType(String args) {
        String[] arguments = args.split(" ");
        if (arguments.length > 2) {
            throw new IllegalArgumentException("Expected 0-2 arguments, but received " + arguments.length + ": " + Arrays.toString(arguments));
        } else {
            if (arguments.length > 0) {
                double min = Double.parseDouble(arguments[0]);
                if (arguments.length > 1) {
                    double max = Double.parseDouble(arguments[1]);
                    return DoubleArgumentType.doubleArg(min, max);
                }
                return DoubleArgumentType.doubleArg(min);
            }
            return DoubleArgumentType.doubleArg();
        }
    }

    private static LongArgumentType parseLongArgumentType(String args) {
        String[] arguments = args.split(" ");
        if (arguments.length > 2) {
            throw new IllegalArgumentException("Expected 0-2 arguments, but received " + arguments.length + ": " + Arrays.toString(arguments));
        } else {
            if (arguments.length > 0) {
                long min = Long.parseLong(arguments[0]);
                if (arguments.length > 1) {
                    long max = Long.parseLong(arguments[1]);
                    return LongArgumentType.longArg(min, max);
                }
                return LongArgumentType.longArg(min);
            }
            return LongArgumentType.longArg();
        }
    }

    private static FloatArgumentType parseFloatArgumentType(String args) {
        String[] arguments = args.split(" ");
        if (arguments.length > 2) {
            throw new IllegalArgumentException("Expected 0-2 arguments, but received " + arguments.length + ": " + Arrays.toString(arguments));
        } else {
            if (arguments.length > 0) {
                float min = Float.parseFloat(arguments[0]);
                if (arguments.length > 1) {
                    float max = Float.parseFloat(arguments[1]);
                    return FloatArgumentType.floatArg(min, max);
                }
                return FloatArgumentType.floatArg(min);
            }
            return FloatArgumentType.floatArg();
        }
    }

}
