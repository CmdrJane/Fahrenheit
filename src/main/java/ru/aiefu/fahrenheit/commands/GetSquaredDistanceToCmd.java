package ru.aiefu.fahrenheit.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GetSquaredDistanceToCmd {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(CommandManager.literal("getSquaredDistance").executes(context -> execute(context.getSource())));
    }
    private static int execute (ServerCommandSource source) throws CommandSyntaxException {
        if(source.getEntity() instanceof PlayerEntity){
            source.sendFeedback(new LiteralText("Squared distance to center of block: " + source.getPlayer().raycast(20.0D, 0.0F, true).squaredDistanceTo(source.getPlayer())), false);
        }
        else {
            source.sendFeedback(new LiteralText("Can be executed only by player"), true);
        }
        return 0;
    }
}
