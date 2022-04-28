package gdavid.phi.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(bus = Bus.MOD)
public class ModCapabilities {
	
	@CapabilityInject(IAccelerationCapability.class)
	public static Capability<IAccelerationCapability> acceleration;
	
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(IAccelerationCapability.class, new Storage<>(),
				AccelerationCapability::new);
	}
	
	public static class Provider<T> implements ICapabilityProvider {
		
		final Capability<T> capability;
		final T instance;
		
		public Provider(Capability<T> capability, T instance) {
			this.capability = capability;
			this.instance = instance;
		}
		
		@Override
		public <U> LazyOptional<U> getCapability(Capability<U> capability, Direction direction) {
			return this.capability.orEmpty(capability, LazyOptional.of(() -> instance));
		}
		
	}
	
	private static class Storage<T> implements Capability.IStorage<T> {
		
		@Override
		public INBT writeNBT(Capability<T> capability, T instance, Direction direction) {
			if (instance instanceof INBTSerializable) {
				return ((INBTSerializable<?>) instance).serializeNBT();
			}
			return null;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void readNBT(Capability<T> capability, T instance, Direction direction, INBT nbt) {
			if (instance instanceof INBTSerializable) {
				((INBTSerializable<INBT>) instance).deserializeNBT(nbt);
			}
		}
		
	}
	
}
