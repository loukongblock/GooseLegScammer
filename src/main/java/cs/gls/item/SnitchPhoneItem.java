package cs.gls.item;

import cs.gls.entity.GooseAuntEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 举报手机物品。
 * 右键使用后，找到离玩家最近的鹅腿阿姨，将其改名为"鸭腿阿姨"，并涨价到20绿宝石只卖鸭腿。
 */
public class SnitchPhoneItem extends Item {
    public SnitchPhoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.CONSUME;
        }

        List<GooseAuntEntity> aunts = level.getEntitiesOfClass(GooseAuntEntity.class, player.getBoundingBox().inflate(64));
        GooseAuntEntity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (GooseAuntEntity aunt : aunts) {
            double dist = player.distanceTo(aunt);
            if (dist < closestDist) {
                closestDist = dist;
                closest = aunt;
            }
        }

        if (closest != null) {
            closest.snitch();
            player.displayClientMessage(
                Component.literal("[系统] ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(Component.literal("已拨打 12345 举报热线，该鹅腿阿姨已被整改为鸭腿阿姨！")
                        .withStyle(ChatFormatting.WHITE)),
                false
            );
            return InteractionResult.SUCCESS;
        } else {
            player.displayClientMessage(
                Component.literal("[系统] ")
                    .withStyle(ChatFormatting.RED)
                    .append(Component.literal("附近 64 格内没有鹅腿阿姨可以举报！")
                        .withStyle(ChatFormatting.WHITE)),
                false
            );
            return InteractionResult.FAIL;
        }
    }
}
