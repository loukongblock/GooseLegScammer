package cs.gls.block;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 鹅腿阿姨小摊的菜单（ScreenHandler）。
 */
public class StallMenu extends AbstractContainerMenu {

    private final Container container;

    /** 客户端构造函数 */
    public StallMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(4));
    }

    /** 服务端构造函数 */
    public StallMenu(int containerId, Inventory playerInventory, Container container) {
        super(GooseLegMenuTypes.STALL_MENU, containerId);
        this.container = container;
        checkContainerSize(container, 4);

        // 4 个输入槽（横排）
        for (int i = 0; i < 4; i++) {
            this.addSlot(new Slot(container, i, 44 + i * 18, 20));
        }

        // 玩家背包 3x9
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }

        // 玩家快捷栏
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 109));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && this.container instanceof StallBlockEntity stall) {
            stall.tryCraft();
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index < 4) {
                if (!this.moveItemStackTo(itemStack2, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack2, 0, 4, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }
}
