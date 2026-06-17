package cs.gls.entity;

import cs.gls.item.GooseLegItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * 鹅腿阿姨实体。
 * 前6次交易给鹅腿，第7-12次给鸭腿，第13次以后给发绿的鸭腿。
 * 被举报后变成"鸭腿阿姨"，只卖鸭腿，涨价到20绿宝石。
 * 被绑定到摊贩方块，不能离开超过10格。
 */
public class GooseAuntEntity extends PathfinderMob {
    private static final EntityDataAccessor<Integer> TRADE_COUNT =
        SynchedEntityData.defineId(GooseAuntEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> SNITCHED =
        SynchedEntityData.defineId(GooseAuntEntity.class, EntityDataSerializers.BOOLEAN);

    private BlockPos stallPos;

    public GooseAuntEntity(EntityType<? extends GooseAuntEntity> entityType, Level level) {
        super(entityType, level);
        if (getCustomName() == null) {
            setCustomName(Component.literal("鹅腿阿姨"));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TRADE_COUNT, 0);
        builder.define(SNITCHED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TradeCount", getTradeCount());
        compound.putBoolean("Snitched", isSnitched());
        if (stallPos != null) {
            compound.putInt("StallX", stallPos.getX());
            compound.putInt("StallY", stallPos.getY());
            compound.putInt("StallZ", stallPos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setTradeCount(compound.getInt("TradeCount"));
        if (compound.contains("Snitched")) {
            setSnitched(compound.getBoolean("Snitched"));
        }
        if (compound.contains("StallX")) {
            stallPos = new BlockPos(compound.getInt("StallX"), compound.getInt("StallY"), compound.getInt("StallZ"));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && stallPos != null) {
            double distSqr = (double) this.blockPosition().distSqr(stallPos);
            if (distSqr > 10.0 * 10.0) {
                this.getNavigation().moveTo(
                    stallPos.getX() + 0.5,
                    stallPos.getY(),
                    stallPos.getZ() + 0.5,
                    1.5
                );
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.CONSUME;
        }

        ItemStack stack = player.getItemInHand(hand);

        if (isSnitched()) {
            // 被举报后：只卖鸭腿，20绿宝石
            if (stack.is(Items.EMERALD) && stack.getCount() >= 20) {
                ItemStack reward = new ItemStack(GooseLegItems.DUCK_LEG);
                stack.shrink(20);
                if (!player.addItem(reward)) {
                    player.drop(reward, false);
                }
                this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, this.getSoundSource(), 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }

            player.displayClientMessage(
                Component.literal("[鸭腿阿姨] ")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("手持20个绿宝石右键买鸭腿，原材料已写清楚，介意请勿下单！")
                        .withStyle(ChatFormatting.RED)),
                false
            );
            return InteractionResult.SUCCESS;
        }

        // 正常模式：前6次鹅腿，7-12次鸭腿，13次以后发绿的鸭腿，16绿宝石
        if (stack.is(Items.EMERALD) && stack.getCount() >= 16) {
            int count = getTradeCount();
            ItemStack reward;
            String rewardName;
            if (count < 6) {
                reward = new ItemStack(GooseLegItems.GOOSE_LEG);
                rewardName = "鹅腿";
            } else if (count < 12) {
                reward = new ItemStack(GooseLegItems.DUCK_LEG);
                rewardName = "鸭腿";
            } else {
                reward = new ItemStack(GooseLegItems.GREEN_DUCK_LEG);
                rewardName = "发绿的鸭腿";
            }

            stack.shrink(16);
            if (!player.addItem(reward)) {
                player.drop(reward, false);
            }
            setTradeCount(count + 1);
            this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, this.getSoundSource(), 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }

        player.displayClientMessage(
            Component.literal("[鹅腿阿姨] ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal("手持16个绿宝石右键换一个腿")
                    .withStyle(ChatFormatting.RED)),
            false
        );
        return InteractionResult.SUCCESS;
    }

    public void snitch() {
        if (isSnitched()) {
            return; // 已被举报过，不再重复处理
        }
        setSnitched(true);
        setCustomName(Component.literal("鸭腿阿姨"));
        if (!this.level().isClientSide) {
            String[] lines = {
                "大家好和大家说一下国贸本周不过来了下周待定，",
                "被群里某位上班精英举报正在配合相关部门工作，请大家见谅，",
                "不管是哪位有哪里不满意或者不顺心的话可以第一时间和我说一声",
                "阿姨请客也没关系的哈，做点小本生意讨一口生活请不要为难呢，",
                "非常感谢你们的支持呀，",
                "非常感谢你们对阿姨的支持与信任，",
                "感谢你们原材料是鸭腿以后都会给大家写清楚介意请勿下单"
            };
            for (String line : lines) {
                this.level().getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("[鸭腿阿姨] ")
                        .withStyle(ChatFormatting.GOLD)
                        .append(Component.literal(line).withStyle(ChatFormatting.WHITE)),
                    false
                );
            }
        }
    }

    public boolean isSnitched() {
        return this.entityData.get(SNITCHED);
    }

    public void setSnitched(boolean snitched) {
        this.entityData.set(SNITCHED, snitched);
    }

    public void setStallPos(BlockPos pos) {
        this.stallPos = pos;
    }

    public BlockPos getStallPos() {
        return this.stallPos;
    }

    public int getTradeCount() {
        return this.entityData.get(TRADE_COUNT);
    }

    public void setTradeCount(int count) {
        this.entityData.set(TRADE_COUNT, count);
    }
}
