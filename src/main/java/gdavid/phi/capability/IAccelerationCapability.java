package gdavid.phi.capability;

import net.minecraft.entity.Entity;
import vazkii.psi.api.internal.Vector3;

public interface IAccelerationCapability {
	
	Vector3 getAcceleration();
	
	void addAcceleration(Vector3 acceleration, int duration);
	
	void tick(Entity entity);
	
}
