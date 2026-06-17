package cs.gls;

import cs.gls.block.GooseLegBlockEntityTypes;
import cs.gls.block.GooseLegBlocks;
import cs.gls.block.GooseLegMenuTypes;
import cs.gls.block.StallBlockEntity;
import cs.gls.entity.GooseLegEntityTypes;
import cs.gls.item.GooseLegItems;
import cs.gls.item.ModItemGroups;
import cs.gls.world.gen.StallWorldGen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cs.gls.command.GooseLegCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class GooseLegScammer implements ModInitializer {
	public static final String MOD_ID = "gooselegscammer";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Goose Leg Scammer mod initialized!");
		GooseLegBlocks.init();
		GooseLegItems.init();
		ModItemGroups.init();
		GooseLegEntityTypes.init();
		GooseLegBlockEntityTypes.init();
		GooseLegMenuTypes.init();
		StallWorldGen.init();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			GooseLegCommands.register(dispatcher);
		});
	}
}
