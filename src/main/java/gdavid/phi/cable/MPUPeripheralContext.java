package gdavid.phi.cable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import vazkii.psi.api.internal.Vector3;

public class MPUPeripheralContext implements PeripheralContext {
	
	public final Level level;
	public final BlockPos pos;
	
	public MPUPeripheralContext(Level level, BlockPos pos) {
		this.level = level;
		this.pos = pos;
	}
	
	@Override
	public <T extends ICableConnected> T getPeripheral(Class<T> clazz, Vector3 dir) {
		var side = Direction.getNearest(dir.x, dir.y, dir.z);
		BlockPos connected = CableNetwork.getController(level, pos, side);
		if (connected == null) return null;
		var tile = level.getBlockEntity(connected);
		return clazz.isInstance(tile) ? (T) tile : null;
	}
	
}
