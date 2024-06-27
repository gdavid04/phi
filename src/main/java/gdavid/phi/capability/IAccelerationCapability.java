package gdavid.phi.capability;

import net.minecraft.world.entity.Entity;
import vazkii.psi.api.internal.Vector3;

public interface IAccelerationCapability {
	
	Vector3 getAcceleration(Entity entity);
	
	void addAcceleration(Vector3 acceleration, int duration);
	
	void addAccelerationTowardsPoint(Vector3 center, double power, int duration);
	
	void tick(Entity entity);
	
}
