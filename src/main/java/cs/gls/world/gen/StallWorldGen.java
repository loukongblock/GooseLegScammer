package cs.gls.world.gen;

import cs.gls.GooseLegScammer;
import cs.gls.block.GooseLegBlocks;
import cs.gls.entity.GooseAuntEntity;
import cs.gls.entity.GooseLegEntityTypes;
import cs.gls.world.gen.feature.StallFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * 鹅腿阿姨摊贩世界生成注册。
 * 两种生成方式：
 * 1. 普通世界：通过 BiomeModifications 在 worldgen 阶段生成
 * 2. 超平坦 / 已有世界：通过 ServerTickEvents 定时在玩家附近补充生成
 */
public class StallWorldGen {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(GooseLegScammer.MOD_ID, "stall");

    public static final Feature<NoneFeatureConfiguration> STALL_FEATURE = Registry.register(
        BuiltInRegistries.FEATURE,
        ID,
        new StallFeature(NoneFeatureConfiguration.CODEC)
    );

    public static void init() {
        ResourceKey<Registry<PlacedFeature>> placedFeatureRegistry = ResourceKey.createRegistryKey(
            ResourceLocation.fromNamespaceAndPath("minecraft", "worldgen/placed_feature")
        );
        ResourceKey<PlacedFeature> placedFeatureKey = ResourceKey.create(placedFeatureRegistry, ID);

        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Decoration.SURFACE_STRUCTURES,
            placedFeatureKey
        );

        // 补充生成：适用于超平坦和已有世界
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (world.isClientSide()) return;
            long gameTime = world.getGameTime();
            // 每 10 秒 (200 ticks) 尝试一次
            if (gameTime % 200L == 0 && gameTime > 0) {
                GooseLegScammer.LOGGER.info("StallWorldGen tick triggered at gameTime={}, players={}", gameTime, world.players().size());
                for (ServerPlayer player : world.players()) {
                    trySpawnNearPlayer(world, player);
                }
            }
        });
    }

    private static void trySpawnNearPlayer(ServerLevel world, ServerPlayer player) {
        int angle = world.random.nextInt(360);
        int distance = 20 + world.random.nextInt(20); // 20~40 格，确保在加载范围内
        double rad = Math.toRadians(angle);
        int x = player.blockPosition().getX() + (int) (Math.cos(rad) * distance);
        int z = player.blockPosition().getZ() + (int) (Math.sin(rad) * distance);
        int startY = player.blockPosition().getY() + 50;

        GooseLegScammer.LOGGER.info("Tick spawn: trying x={}, z={}, startY={}, playerY={}", x, z, startY, player.blockPosition().getY());

        BlockPos stallPos = findSurface(world, x, z, startY);
        GooseLegScammer.LOGGER.info("Tick spawn: findSurface returned {}", stallPos);

        if (stallPos == null) {
            GooseLegScammer.LOGGER.info("Tick spawn: no surface found, aborting");
            return;
        }

        // 检查周围是否已有摊贩
        for (int dx = -8; dx <= 8; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -8; dz <= 8; dz++) {
                    if (world.getBlockState(stallPos.offset(dx, dy, dz)).is(GooseLegBlocks.STALL_BLOCK)) {
                        GooseLegScammer.LOGGER.info("Tick spawn: stall already nearby at {}, aborting", stallPos);
                        return;
                    }
                }
            }
        }

        // 生成摊贩
        world.setBlock(stallPos, GooseLegBlocks.STALL_BLOCK.defaultBlockState(), 3);
        GooseLegScammer.LOGGER.info("Tick-based stall spawned at {}", stallPos);

        // 生成鹅腿阿姨
        GooseAuntEntity aunt = new GooseAuntEntity(GooseLegEntityTypes.GOOSE_AUNT, world);
        aunt.setPos(stallPos.getX() + 0.5, stallPos.getY() + 1, stallPos.getZ() + 0.5);
        aunt.setStallPos(stallPos);
        world.addFreshEntity(aunt);

        GooseLegScammer.LOGGER.info("Tick-based goose aunt spawned at {}", stallPos);
    }

    private static BlockPos findSurface(ServerLevel world, int x, int z, int startY) {
        int maxY = Math.min(startY, 384);
        int minY = -64;
        BlockPos firstBlock = null;

        // 第一步：找到第一个非空气方块
        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = world.getBlockState(pos);
            if (!state.isAir()) {
                firstBlock = pos;
                GooseLegScammer.LOGGER.info("findSurface: first non-air block at y={} is {}", y, state.getBlock().getDescriptionId());
                break;
            }
        }

        if (firstBlock == null) {
            GooseLegScammer.LOGGER.info("findSurface: no blocks found at x={}, z={}", x, z);
            return null;
        }

        // 第二步：从 firstBlock 向下找到第一个固体方块
        for (int y = firstBlock.getY(); y >= minY; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = world.getBlockState(pos);
            if (state.isSolid()) {
                GooseLegScammer.LOGGER.info("findSurface: solid block at y={} is {}", y, state.getBlock().getDescriptionId());
                return pos.above();
            }
        }

        GooseLegScammer.LOGGER.info("findSurface: no solid block found below firstBlock at x={}, z={}", x, z);
        return null;
    }
}
