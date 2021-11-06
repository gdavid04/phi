package gdavid.phi.entity;

import static net.minecraft.entity.EntityClassification.MISC;

import gdavid.phi.Phi;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD)
public class ModEntities {
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<EntityType<?>> event) {
		event.getRegistry().registerAll(
				EntityType.Builder.create((EntityType.IFactory<PsionWaveEntity>) PsionWaveEntity::new, MISC)
						.setTrackingRange(256).setUpdateInterval(10).setShouldReceiveVelocityUpdates(false).size(1, 1)
						.immuneToFire().build("").setRegistryName(Phi.modId, PsionWaveEntity.id),
				EntityType.Builder.create((EntityType.IFactory<PsiProjectileEntity>) PsiProjectileEntity::new, MISC)
						.setTrackingRange(256).setUpdateInterval(10).setShouldReceiveVelocityUpdates(false)
						.size(0.4f, 0.4f).immuneToFire().build("").setRegistryName(Phi.modId, PsiProjectileEntity.id),
				EntityType.Builder.create((EntityType.IFactory<MarkerEntity>) MarkerEntity::new, MISC)
						.setTrackingRange(256).setUpdateInterval(10).size(1, 1).immuneToFire().build("")
						.setRegistryName(Phi.modId, MarkerEntity.id));
	}
	
}
