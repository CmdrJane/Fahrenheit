package ru.aiefu.fahrenheit.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import ru.aiefu.fahrenheit.IPlayerMixins;

public class DebugCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(CommandManager.literal("setWater").executes(context -> execute(context.getSource())));
    }
    private static int execute (ServerCommandSource source) throws CommandSyntaxException {
        ((IPlayerMixins)source.getPlayer()).getEnviroManager().setWater(5);
        return 0;
    }
}
