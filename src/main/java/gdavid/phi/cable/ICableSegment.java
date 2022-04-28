package gdavid.phi.cable;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public interface ICableSegment {
	
	@Nullable
	BlockPos getConnection();
	
	void setConnection(@Nullable BlockPos connection, Predicate<BlockPos> connected);
	
	Iterable<BlockPos> getNeighbours();
	
	boolean canConnect(Direction side);
	
}
