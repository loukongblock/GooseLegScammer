package cs.gls.item;

import cs.gls.GooseLegScammer;
import cs.gls.block.GooseLegBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * 鹅腿骗子模组创造模式物品栏注册类。
 */
public class ModItemGroups {
    public static final CreativeModeTab GOOSE_LEG_SCAMMER = register("goose_leg_scammer",
        CreativeModeTab.builder(CreativeModeTab.Row.TOP, 6)
            .title(Component.translatable("itemGroup.gooselegscammer"))
            .icon(() -> new ItemStack(GooseLegItems.GOOSE_LEG))
            .displayItems((params, output) -> {
                output.accept(new ItemStack(GooseLegItems.GOOSE_LEG));
                output.accept(new ItemStack(GooseLegItems.DUCK_LEG));
                output.accept(new ItemStack(GooseLegItems.GREEN_DUCK_LEG));
                output.accept(new ItemStack(GooseLegItems.GOOSE_AUNT_SPAWN_EGG));
                output.accept(new ItemStack(GooseLegBlocks.STALL_BLOCK_ITEM));
                output.accept(new ItemStack(GooseLegItems.SNITCH_PHONE));
            })
            .build());

    private static CreativeModeTab register(String name, CreativeModeTab tab) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(GooseLegScammer.MOD_ID, name), tab);
    }

    public static void init() {
        // 静态字段会在类加载时自动注册到 Minecraft 注册表
    }
}
