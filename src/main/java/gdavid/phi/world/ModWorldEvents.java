package gdavid.phi.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModWorldEvents {
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void attachFeatures(BiomeLoadingEvent event) {
		if (event.getCategory() == Biome.Category.THEEND) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ModWorld.psionicDustOre);
		}
	}
	
}
