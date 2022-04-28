package gdavid.phi.cable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CableNetwork {
	
	public static @Nullable BlockPos getController(World world, BlockPos pos, Direction side) {
		return getControllerInternal(world, pos, side, true);
	}
	
	public static @Nullable BlockPos getControllerInternal(World world, BlockPos pos, Direction side,
			boolean stepDown) {
		BlockPos opos = pos.offset(side);
		TileEntity tile = world.getTileEntity(opos);
		if (tile instanceof ICableSegment) {
			if (((ICableSegment) tile).canConnect(side.getOpposite())) {
				return ((ICableSegment) tile).getConnection();
			}
		} else if (stepDown) {
			if (tile instanceof ICableConnected) {
				if (((ICableConnected) tile).isController()) return opos;
			} else if (side.getAxis() != Axis.Y) {
				return getControllerInternal(world, pos.offset(Direction.DOWN), side, false);
			}
		}
		return null;
	}
	
	public static void rebuild(World world, BlockPos pos) {
		if (world.getTileEntity(pos) instanceof ICableSegment) {
			rebuildInternal(world, pos);
		} else {
			for (Direction dir : Direction.values()) {
				rebuildInternal(world, pos.offset(dir));
				if (dir.getAxis() != Axis.Y) {
					rebuildInternal(world, pos.offset(dir).offset(Direction.UP));
					rebuildInternal(world, pos.offset(dir).offset(Direction.DOWN));
				}
			}
		}
	}
	
	static void rebuildInternal(World world, BlockPos pos) {
		HashSet<BlockPos> matched = new HashSet<>();
		List<ICableSegment> cables = new ArrayList<>();
		@Nullable
		BlockPos controller = null;
		boolean valid = true;
		Stack<TileEntity> s = new Stack<>();
		tryAdd(s, matched, world, pos);
		while (!s.isEmpty()) {
			TileEntity tile = s.pop();
			if (tile instanceof ICableSegment) {
				cables.add((ICableSegment) tile);
				for (BlockPos opos : ((ICableSegment) tile).getNeighbours()) {
					tryAdd(s, matched, world, opos);
				}
			} else if (tile instanceof ICableConnected) {
				if (((ICableConnected) tile).isController()) {
					if (controller == null && valid) {
						controller = tile.getPos();
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
	
	static void tryAdd(Stack<TileEntity> s, HashSet<BlockPos> matched, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof ICableSegment || tile instanceof ICableConnected) {
			if (matched.add(pos)) s.push(tile);
		}
	}
	
}
