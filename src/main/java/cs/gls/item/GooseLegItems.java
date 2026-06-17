package cs.gls.item;

import cs.gls.GooseLegScammer;
import cs.gls.entity.GooseLegEntityTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

/**
 * 鹅腿骗子模组物品注册类。
 */
public class GooseLegItems {
    public static final Item GOOSE_LEG = register("goose_leg",
        new Item.Properties()
            .food(new FoodProperties.Builder()
                .nutrition(10)
                .saturationModifier(0.6f)
                .build()));

    public static final Item DUCK_LEG = register("duck_leg",
        new Item.Properties()
            .food(new FoodProperties.Builder()
                .nutrition(6)
                .saturationModifier(0.3f)
                .build()));

    public static final Item GREEN_DUCK_LEG = registerPoisonous("green_duck_leg",
        new Item.Properties()
            .food(new FoodProperties.Builder()
                .nutrition(0)
                .saturationModifier(0.0f)
                .alwaysEdible()
                .build()), 2.0f);

    public static final Item GOOSE_AUNT_SPAWN_EGG = registerSpawnEgg("goose_aunt_spawn_egg",
        GooseLegEntityTypes.GOOSE_AUNT);

    public static final Item SNITCH_PHONE = registerSnitchPhone("snitch_phone");

    private static Item registerSnitchPhone(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GooseLegScammer.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), id);
        Item item = new SnitchPhoneItem(new Item.Properties().setId(key).stacksTo(1));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    private static Item register(String name, Item.Properties properties) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GooseLegScammer.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), id);
        Item item = new Item(properties.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    private static Item registerPoisonous(String name, Item.Properties properties, float damageAmount) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GooseLegScammer.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), id);
        Item item = new PoisonousFoodItem(properties.setId(key), damageAmount);
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    private static Item registerSpawnEgg(String name, net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob> entityType) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GooseLegScammer.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), id);
        Item item = new SpawnEggItem(entityType, new Item.Properties().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void init() {
        // 静态字段会在类加载时自动注册到 Minecraft 注册表
    }
}
