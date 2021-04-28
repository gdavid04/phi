package gdavid.phi.entity;

import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import static net.minecraft.entity.EntityClassification.MISC;

import gdavid.phi.Phi;

@EventBusSubscriber(bus = Bus.MOD)
public class ModEntities {
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<EntityType<?>> event) {
		event.getRegistry().registerAll(
			EntityType.Builder.create((EntityType.IFactory<PsionWaveEntity>) PsionWaveEntity::new, MISC)
				.setTrackingRange(256)
				.setUpdateInterval(10)
				.setShouldReceiveVelocityUpdates(false)
				.size(1, 1)
				.build("").setRegistryName(Phi.modId, PsionWaveEntity.id)
		);
	}
	
}
