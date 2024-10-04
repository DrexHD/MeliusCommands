package me.drex.meliuscommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import me.drex.meliuscommands.util.PathCache;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.util.List;

public class MeliusCommandsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("melius-commands")
                .requires(source -> source.hasPermission(2))
                .then(
                    Commands.literal("path")
                        .then(
                            Commands.argument("command", StringArgumentType.greedyString())
                                .executes(context -> sendCommandPath(dispatcher, context))
                        )
                )
        );
    }

    public static int sendCommandPath(CommandDispatcher<CommandSourceStack> dispatcher, CommandContext<CommandSourceStack> context) {
        String command = StringArgumentType.getString(context, "command");
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        ParseResults<CommandSourceStack> parseResults = dispatcher.parse(command, context.getSource());
        if (parseResults.getReader().canRead()) {
            context.getSource().sendFailure(Component.literal("not a valid command"));
        } else {
            CommandContextBuilder<CommandSourceStack> lastChild = parseResults.getContext().getLastChild();
            List<ParsedCommandNode<CommandSourceStack>> nodes = lastChild.getNodes();
            assert !nodes.isEmpty();
            CommandNode<CommandSourceStack> node = nodes.get(nodes.size() - 1).getNode();
            String path = PathCache.getPath(dispatcher, node);
            context.getSource().sendSuccess(() -> Component.literal(path).withStyle(style ->
                style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, path))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy to clipboard!")))
            ), false);
        }
        return 1;
    }

}
