package gdavid.phi.block;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD)
public class ModBlocks {
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
			new ShadowBlock()
		);
	}
	
}
