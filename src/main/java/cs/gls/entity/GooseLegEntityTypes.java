package cs.gls.entity;

import cs.gls.GooseLegScammer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

/**
 * 鹅腿骗子模组实体类型注册类。
 */
public class GooseLegEntityTypes {
    public static final EntityType<GooseAuntEntity> GOOSE_AUNT = register(
        "goose_aunt",
        EntityType.Builder.of(GooseAuntEntity::new, MobCategory.MISC)
            .sized(0.6f, 1.95f)
    );

    @SuppressWarnings("unchecked")
    private static EntityType<GooseAuntEntity> register(String name, EntityType.Builder builder) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GooseLegScammer.MOD_ID, name);
        ResourceKey<EntityType<?>> key = ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), id);
        return (EntityType<GooseAuntEntity>) Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(key));
    }

    public static void init() {
        FabricDefaultAttributeRegistry.register(GOOSE_AUNT, DefaultAttributes.getSupplier(EntityType.VILLAGER));
    }
}
