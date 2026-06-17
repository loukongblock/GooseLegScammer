package cs.gls.client;

import cs.gls.block.GooseLegMenuTypes;
import cs.gls.entity.GooseLegEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

/**
 * 客户端初始化类，注册实体渲染器和 GUI 屏幕。
 */
public class GooseLegScammerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(GooseLegEntityTypes.GOOSE_AUNT, GooseAuntRenderer::new);
        MenuScreens.register(GooseLegMenuTypes.STALL_MENU, StallScreen::new);
    }
}
