package gdavid.phi.world;

import gdavid.phi.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModWorld {
	
	public static ConfiguredFeature<?, ?> psionicDustOre;
	
	@SubscribeEvent
	public static void registerFeatures(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			psionicDustOre = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "psionic_dust_ore",
					Feature.ORE.withConfiguration(new OreFeatureConfig(new BlockMatchRuleTest(Blocks.END_STONE), ModBlocks.psionicDustOre.getDefaultState(), 10))
							.withPlacement(Placement.field_242907_l.configure(new TopSolidRangeConfig(10, 20, 128)))
							.withPlacement(Placement.field_242903_g.configure(NoPlacementConfig.NO_PLACEMENT_CONFIG)).func_242731_b(5));
		});
	}
	
}
