package cs.gls.block;

import cs.gls.GooseLegScammer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * 鹅腿骗子模组方块注册类。
 */
public class GooseLegBlocks {

    public static Block createStallBlockProperties(ResourceKey<Block> key) {
        return new StallBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(2.0f, 3.0f)
            .noOcclusion()
            .setId(key)
        );
    }

    public static final Block STALL_BLOCK;
    public static final Item STALL_BLOCK_ITEM;

    static {
        ResourceLocation stallId = ResourceLocation.fromNamespaceAndPath(GooseLegScammer.MOD_ID, "stall_block");
        ResourceKey<Block> stallKey = ResourceKey.create(BuiltInRegistries.BLOCK.key(), stallId);
        STALL_BLOCK = Registry.register(BuiltInRegistries.BLOCK, stallKey, createStallBlockProperties(stallKey));

        ResourceKey<Item> itemKey = ResourceKey.create(BuiltInRegistries.ITEM.key(), stallId);
        STALL_BLOCK_ITEM = Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(STALL_BLOCK, new Item.Properties().setId(itemKey)));
    }

    public static void init() {
        // 静态字段自动注册
    }
}
