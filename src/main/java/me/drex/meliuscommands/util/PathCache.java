package me.drex.meliuscommands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;

import java.util.HashMap;
import java.util.Map;

public class PathCache {

    private static final Map<CommandNode<CommandSourceStack>, String> PATH_CACHE = new HashMap<>();

    public static String getPath(CommandDispatcher<CommandSourceStack> dispatcher, CommandNode<CommandSourceStack> node) {
        return PATH_CACHE.computeIfAbsent(node, ignored -> String.join(".", dispatcher.getPath(node)));
    }

    public static void invalidate() {
        PATH_CACHE.clear();
    }

}
