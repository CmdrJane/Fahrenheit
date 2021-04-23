package ru.aiefu.fahrenheit.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import ru.aiefu.fahrenheit.IOManager;

public class FahrenheitReloadCfg {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(CommandManager.literal("fahrenheit-reload").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4)).executes(context -> execute(context.getSource())));
    }
    private static int execute (ServerCommandSource source) throws CommandSyntaxException {
        new IOManager().reload();
        source.sendFeedback(new LiteralText("[Fahrenheit] Config reloaded"), true);
        return 0;
    }
}
