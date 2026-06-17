package cs.gls.block;

import cs.gls.GooseLegScammer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

/**
 * 注册所有 MenuType（ScreenHandler 类型）。
 */
public class GooseLegMenuTypes {

    public static final MenuType<StallMenu> STALL_MENU;

    static {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GooseLegScammer.MOD_ID, "stall_menu");
        ResourceKey<MenuType<?>> key = ResourceKey.create(BuiltInRegistries.MENU.key(), id);
        STALL_MENU = Registry.register(BuiltInRegistries.MENU, key,
            new MenuType<>(StallMenu::new, FeatureFlags.DEFAULT_FLAGS));
    }

    public static void init() {
        // 静态字段自动注册
    }
}
