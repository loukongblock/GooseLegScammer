package cs.gls.world.gen.feature;

import com.mojang.serialization.Codec;
import cs.gls.GooseLegScammer;
import cs.gls.block.GooseLegBlocks;
import cs.gls.entity.GooseAuntEntity;
import cs.gls.entity.GooseLegEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * 鹅腿阿姨摊贩生成特征。
 * 在 Overworld 地表随机生成一个摊贩方块和一个鹅腿阿姨。
 */
public class StallFeature extends Feature<NoneFeatureConfiguration> {
    public StallFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        // 超平坦世界的 Heightmap 不可靠，改为手动扫描
        BlockPos stallPos = findSurface(level, origin.getX(), origin.getZ(), 384);
        if (stallPos == null) {
            GooseLegScammer.LOGGER.debug("StallFeature: no solid surface found at {}, {}", origin.getX(), origin.getZ());
            return false;
        }

        // 放置摊贩方块
        level.setBlock(stallPos, GooseLegBlocks.STALL_BLOCK.defaultBlockState(), 3);
        GooseLegScammer.LOGGER.info("StallFeature: spawned stall at {}", stallPos);

        // 生成鹅腿阿姨（绑定到这个摊贩）
        GooseAuntEntity aunt = new GooseAuntEntity(GooseLegEntityTypes.GOOSE_AUNT, level.getLevel());
        aunt.setPos(stallPos.getX() + 0.5, stallPos.getY() + 1, stallPos.getZ() + 0.5);
        aunt.setStallPos(stallPos);
        level.addFreshEntity(aunt);

        return true;
    }

    private BlockPos findSurface(WorldGenLevel level, int x, int z, int startY) {
        int maxY = Math.min(startY, 384);
        int minY = -64;

        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isSolid()) {
                return pos.above();
            }
        }
        return null;
    }
}
