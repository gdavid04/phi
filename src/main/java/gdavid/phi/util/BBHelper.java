package gdavid.phi.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class BBHelper {
	
	public static Vector3d min(AxisAlignedBB bb) {
		return new Vector3d(bb.minX, bb.minY, bb.minZ);
	}
	
	public static Vector3d max(AxisAlignedBB bb) {
		return new Vector3d(bb.maxX, bb.maxY, bb.maxZ);
	}
	
}
