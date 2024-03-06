package gdavid.phi.world;

import gdavid.phi.block.ModBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModWorld {
	
	public static ConfiguredFeature<?, ?> psionicDustOre;
	
	@SubscribeEvent
	public static void registerFeatures(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			psionicDustOre = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "psionic_dust_ore",
					Feature.ORE.configured(new OreConfiguration(new BlockMatchTest(Blocks.END_STONE), ModBlocks.psionicDustOre.defaultBlockState(), 10))
							.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(10, 20, 128)))
							.decorated(FeatureDecorator.SQUARE.configured(NoneDecoratorConfiguration.NONE)).count(5));
		});
	}
	
}
