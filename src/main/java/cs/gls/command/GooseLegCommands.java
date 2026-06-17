package cs.gls.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import cs.gls.block.GooseLegBlocks;
import cs.gls.entity.GooseAuntEntity;
import cs.gls.entity.GooseLegEntityTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 鹅腿骗子模组指令。
 */
public class GooseLegCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("gls")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("spawnstall")
                    .executes(GooseLegCommands::spawnStall))
        );
    }

    private static int spawnStall(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("此指令只能由玩家执行"));
            return 0;
        }

        Level level = player.level();
        BlockPos pos = player.blockPosition().above();

        // 放置摊贩方块
        level.setBlock(pos, GooseLegBlocks.STALL_BLOCK.defaultBlockState(), 3);

        // 生成鹅腿阿姨
        GooseAuntEntity aunt = new GooseAuntEntity(GooseLegEntityTypes.GOOSE_AUNT, level);
        aunt.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        aunt.setStallPos(pos);
        level.addFreshEntity(aunt);

        source.sendSuccess(() -> Component.literal("已在 " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + " 生成鹅腿阿姨摊贩！"), true);
        return 1;
    }
}
