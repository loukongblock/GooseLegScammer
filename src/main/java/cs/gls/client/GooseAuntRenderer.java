package cs.gls.client;

import cs.gls.entity.GooseAuntEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.ResourceLocation;

/**
 * 鹅腿阿姨渲染器，使用自定义纹理。
 */
public class GooseAuntRenderer extends MobRenderer<GooseAuntEntity, VillagerRenderState, VillagerModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("gooselegscammer", "textures/entity/goose_aunt.png");

    public GooseAuntRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    public VillagerRenderState createRenderState() {
        return new VillagerRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(VillagerRenderState state) {
        return TEXTURE;
    }
}
