package gdavid.phi.capability;

import net.minecraft.entity.Entity;
import vazkii.psi.api.internal.Vector3;

public interface IAccelerationCapability {
	
	Vector3 getAcceleration(Entity entity);
	
	void addAcceleration(Vector3 acceleration, int duration);
	
	void addGravity(Vector3 center, double power, int duration);
	
	void tick(Entity entity);
	
}
