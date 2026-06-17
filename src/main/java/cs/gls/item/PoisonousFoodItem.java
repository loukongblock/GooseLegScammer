package cs.gls.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 有毒食物物品，吃完后造成额外伤害。
 */
public class PoisonousFoodItem extends Item {
    private final float damageAmount;

    public PoisonousFoodItem(Properties properties, float damageAmount) {
        super(properties);
        this.damageAmount = damageAmount;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide) {
            livingEntity.hurt(level.damageSources().generic(), this.damageAmount);
        }
        return result;
    }
}
