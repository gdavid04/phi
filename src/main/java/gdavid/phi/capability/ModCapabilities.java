package gdavid.phi.capability;

import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ModCapabilities {
	
	public static Capability<IAccelerationCapability> acceleration = CapabilityManager.get(new CapabilityToken<>() {});
	
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
	
}
