package gdavid.phi.world;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModWorldEvents {
	/* TODO fix before 1.19 release
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void attachFeatures(BiomeLoadingEvent event) {
		if (event.getCategory() == Biome.BiomeCategory.THEEND) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModWorld.psionicDustOre);
		}
	}
	*/
}
