package gdavid.phi.cable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class CableNetwork {
	
	public static @Nullable BlockPos getController(Level world, BlockPos pos, Direction side) {
		return getControllerInternal(world, pos, side, true);
	}
	
	public static @Nullable BlockPos getControllerInternal(Level world, BlockPos pos, Direction side,
			boolean stepDown) {
		BlockPos opos = pos.relative(side);
		BlockEntity tile = world.getBlockEntity(opos);
		if (tile instanceof ICableSegment) {
			if (((ICableSegment) tile).canConnect(side.getOpposite())) {
				return ((ICableSegment) tile).getConnection();
			}
		} else if (stepDown) {
			if (tile instanceof ICableConnected) {
				if (((ICableConnected) tile).isController()) return opos;
			} else if (side.getAxis() != Axis.Y) {
				return getControllerInternal(world, pos.relative(Direction.DOWN), side, false);
			}
		}
		return null;
	}
	
	public static void rebuild(Level world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof ICableSegment) {
			rebuildInternal(world, pos);
		} else {
			for (Direction dir : Direction.values()) {
				rebuildInternal(world, pos.relative(dir));
				if (dir.getAxis() != Axis.Y) {
					rebuildInternal(world, pos.relative(dir).relative(Direction.UP));
					rebuildInternal(world, pos.relative(dir).relative(Direction.DOWN));
				}
			}
		}
	}
	
	static void rebuildInternal(Level world, BlockPos pos) {
		HashSet<BlockPos> matched = new HashSet<>();
		List<ICableSegment> cables = new ArrayList<>();
		@Nullable
		BlockPos controller = null;
		boolean valid = true;
		Stack<BlockEntity> s = new Stack<>();
		tryAdd(s, matched, world, pos);
		while (!s.isEmpty()) {
			BlockEntity tile = s.pop();
			if (tile instanceof ICableSegment) {
				cables.add((ICableSegment) tile);
				for (BlockPos opos : ((ICableSegment) tile).getNeighbours()) {
					tryAdd(s, matched, world, opos);
				}
			} else if (tile instanceof ICableConnected) {
				if (((ICableConnected) tile).isController()) {
					if (controller == null && valid) {
						controller = tile.getBlockPos();
					} else {
						valid = false;
						controller = null;
					}
				}
			}
		}
		for (ICableSegment c : cables) {
			c.setConnection(controller, matched::contains);
		}
	}
	
	static void tryAdd(Stack<BlockEntity> s, HashSet<BlockPos> matched, Level world, BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof ICableSegment || tile instanceof ICableConnected) {
			if (matched.add(pos)) s.push(tile);
		}
	}
	
}
