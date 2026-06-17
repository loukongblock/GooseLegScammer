package cs.gls.block;

import cs.gls.item.GooseLegItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

/**
 * 鹅腿阿姨小摊的 BlockEntity，存储 4 个物品槽，按钮触发合成。
 */
public class StallBlockEntity extends BlockEntity implements Container {

    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    private static final Set<Item> GOOSE_LEG_MEATS = new HashSet<>();

    static {
        GOOSE_LEG_MEATS.add(Items.BEEF);
        GOOSE_LEG_MEATS.add(Items.MUTTON);
        GOOSE_LEG_MEATS.add(Items.PORKCHOP);
    }

    public StallBlockEntity(BlockPos pos, BlockState state) {
        super(GooseLegBlockEntityTypes.STALL_BLOCK_ENTITY, pos, state);
    }

    /**
     * 点击合成按钮时调用。4 个格子都填满才合成，每个格子消耗 1 个。
     */
    public void tryCraft() {
        if (this.level == null || this.level.isClientSide) return;

        // 必须 4 格都有物品
        for (int i = 0; i < 4; i++) {
            if (this.items.get(i).isEmpty()) return;
        }

        boolean hasRottenFlesh = false;
        boolean hasChicken = false;
        boolean allGooseMeats = true;

        for (ItemStack stack : this.items) {
            Item item = stack.getItem();
            if (item == Items.ROTTEN_FLESH) {
                hasRottenFlesh = true;
            } else if (item == Items.CHICKEN) {
                hasChicken = true;
            }
            if (!GOOSE_LEG_MEATS.contains(item)) {
                allGooseMeats = false;
            }
        }

        ItemStack result = ItemStack.EMPTY;
        if (hasRottenFlesh) {
            result = new ItemStack(GooseLegItems.GREEN_DUCK_LEG);
        } else if (hasChicken) {
            result = new ItemStack(GooseLegItems.DUCK_LEG);
        } else if (allGooseMeats) {
            result = new ItemStack(GooseLegItems.GOOSE_LEG);
        }

        if (result.isEmpty()) return;

        // 消耗：每个格子减 1
        for (int i = 0; i < 4; i++) {
            ItemStack stack = this.items.get(i);
            stack.shrink(1);
            if (stack.isEmpty()) {
                this.items.set(i, ItemStack.EMPTY);
            }
        }
        this.setChanged();

        // 吐出结果
        ItemEntity itemEntity = new ItemEntity(this.level,
            this.getBlockPos().getX() + 0.5,
            this.getBlockPos().getY() + 1.0,
            this.getBlockPos().getZ() + 0.5,
            result);
        itemEntity.setDefaultPickUpDelay();
        this.level.addFreshEntity(itemEntity);
    }

    // ===== Container Implementation =====

    @Override
    public int getContainerSize() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null || this.level.getBlockEntity(this.getBlockPos()) != this) {
            return false;
        }
        return player.distanceToSqr(
            this.getBlockPos().getX() + 0.5,
            this.getBlockPos().getY() + 0.5,
            this.getBlockPos().getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    // ===== NBT Serialization =====

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }
}
