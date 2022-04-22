package gdavid.phi.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import javax.annotation.Nullable;

import gdavid.phi.block.CableBlock;
import gdavid.phi.block.ModBlocks;
import gdavid.phi.block.tile.CableTile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CableNetwork {
	
	public static void rebuild(World world, BlockPos pos) {
		if (world.getTileEntity(pos) instanceof CableTile) {
			rebuildInternal(world, pos);
		} else {
			for (Direction dir : Direction.values()) rebuildInternal(world, pos.offset(dir));
		}
	}
	
	static void rebuildInternal(World world, BlockPos pos) {
		HashSet<BlockPos> seen = new HashSet<>(), matched = new HashSet<>();
		List<CableTile> cables = new ArrayList<>();
		@Nullable BlockPos controller = null;
		boolean valid = true;
		Stack<BlockPos> s = new Stack<>();
		s.add(pos);
		seen.add(pos);
		while (!s.isEmpty()) {
			BlockPos cur = s.pop();
			TileEntity tile = world.getTileEntity(cur);
			if (tile instanceof CableTile) {
				matched.add(cur);
				cables.add((CableTile) tile);
				for (Direction dir : Direction.values()) {
					if (dir == Direction.UP) continue;
					BlockPos opos = cur.offset(dir);
					if (!seen.contains(opos)) {
						if (dir != Direction.DOWN || world.getBlockState(opos).getBlock() != ModBlocks.cable) {
							s.push(opos);
							seen.add(opos);
						}
					}
				}
			} else if (tile instanceof ICableConnected) {
				matched.add(cur);
				if (controller == null) { 
					controller = cur;
				} else {
					valid = false;
					controller = null;
				}
			}
		}
		for (CableTile c : cables) {
			c.connected = controller;
			c.markDirty();
			BlockState state = c.getBlockState();
			state = state.with(CableBlock.online, valid && controller != null);
			for (Direction dir : Plane.HORIZONTAL) {
				BlockPos opos = c.getPos().offset(dir);
				state = state.with(CableBlock.sides.get(dir), matched.contains(opos));
			}
			world.setBlockState(c.getPos(), state);
		}
	}
	
	public interface ICableConnected {
	}
	
}
