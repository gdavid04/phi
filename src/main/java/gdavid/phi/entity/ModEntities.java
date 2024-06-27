package gdavid.phi.entity;

import gdavid.phi.Phi;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;

import static net.minecraft.world.entity.MobCategory.MISC;

@EventBusSubscriber(bus = Bus.MOD)
public class ModEntities {
	
	@SubscribeEvent
	public static void init(RegisterEvent event) {
		event.register(Keys.ENTITY_TYPES, handler -> {
			handler.register(PsionWaveEntity.id, EntityType.Builder.of((EntityType.EntityFactory<PsionWaveEntity>) PsionWaveEntity::new, MISC)
					.setTrackingRange(256).setUpdateInterval(10).setShouldReceiveVelocityUpdates(false).sized(1, 1)
					.fireImmune().build(""));
			handler.register(PsiProjectileEntity.id, EntityType.Builder.of((EntityType.EntityFactory<PsiProjectileEntity>) PsiProjectileEntity::new, MISC)
					.setTrackingRange(256).setUpdateInterval(10).setShouldReceiveVelocityUpdates(false)
					.sized(0.4f, 0.4f).fireImmune().build(""));
			handler.register(MarkerEntity.id, EntityType.Builder.of((EntityType.EntityFactory<MarkerEntity>) MarkerEntity::new, MISC)
					.setTrackingRange(256).setUpdateInterval(10).sized(1, 1).fireImmune().build(""));
			handler.register(SpiritEntity.id, EntityType.Builder.of((EntityType.EntityFactory<SpiritEntity>) SpiritEntity::new, MISC)
					.setTrackingRange(256).setUpdateInterval(10).sized(0.8f, 0.8f).fireImmune().build(""));
		});
	}
	
}
