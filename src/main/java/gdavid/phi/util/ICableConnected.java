package gdavid.phi.util;

import javax.annotation.Nullable;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * A tile entity that can connect to cables.
 */
public interface ICableConnected {
	
	/**
	 * Gets if adjacent cables should connect to the tile entity.
	 */
	public boolean connectsInDirection(Direction dir);
	
	/**
	 * Attempt to create a connection in the given direction.
	 * @return whether a connection was successfully made
	 */
	public boolean connect(Direction dir);
	
	public void disconnect(Direction dir);
	
	public @Nullable BlockPos getConnected(Direction dir);
	
}
