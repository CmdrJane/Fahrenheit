package ru.aiefu.fahrenheit.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class GetDistanceTo {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(CommandManager.literal("getDistanceTo").executes(context -> execute(context.getSource())));
    }
    private static int execute (ServerCommandSource source) throws CommandSyntaxException {
        if(source.getEntity() instanceof PlayerEntity){
            source.sendFeedback(new LiteralText("Distance to block: " + Math.sqrt(source.getPlayer().raycast(20.0D, 0.0F, true).squaredDistanceTo(source.getPlayer()))), false);
        }
        else {
            source.sendFeedback(new LiteralText("Can be executed only by player"), true);
        }
        return 0;
    }
}
