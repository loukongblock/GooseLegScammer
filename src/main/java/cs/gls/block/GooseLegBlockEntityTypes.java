package cs.gls.block;

import cs.gls.GooseLegScammer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * 注册所有 BlockEntityType。
 */
public class GooseLegBlockEntityTypes {

    public static final BlockEntityType<StallBlockEntity> STALL_BLOCK_ENTITY;

    static {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GooseLegScammer.MOD_ID, "stall_block_entity");
        ResourceKey<BlockEntityType<?>> key = ResourceKey.create(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), id);
        STALL_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key,
            FabricBlockEntityTypeBuilder.create(StallBlockEntity::new, GooseLegBlocks.STALL_BLOCK).build());
    }

    public static void init() {
        // 静态字段自动注册
    }
}
