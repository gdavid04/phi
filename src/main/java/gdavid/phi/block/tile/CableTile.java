package gdavid.phi.block.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import gdavid.phi.block.CableBlock;
import gdavid.phi.block.CableBlock.CableSide;
import gdavid.phi.cable.ICableConnected;
import gdavid.phi.cable.ICableSegment;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;

public class CableTile extends TileEntity implements ICableSegment {
	
	public static TileEntityType<CableTile> type;
	
	public static final String tagConnection = "connection";
	
	public @Nullable BlockPos connected = null;
	
	public CableTile() {
		super(type);
	}
	
	@Override
	public BlockPos getConnection() {
		return connected;
	}
	
	@Override
	public void setConnection(@Nullable BlockPos connection, Predicate<BlockPos> connected) {
		this.connected = connection;
		markDirty();
		BlockState state = getBlockState();
		state = state.with(CableBlock.online, connection != null);
		for (Direction dir : Plane.HORIZONTAL) {
			CableSide side = CableSide.none;
			if (connected.test(pos.offset(dir).offset(Direction.UP))) side = CableSide.up;
			else if (connected.test(pos.offset(dir))
					|| connected.test(pos.offset(dir).offset(Direction.DOWN))) side = CableSide.side;
			state = state.with(CableBlock.sides.get(dir), side);
		}
		world.setBlockState(pos, state);
	}
	
	@Override
	public Iterable<BlockPos> getNeighbours() {
		List<BlockPos> res = new ArrayList<>();
		for (Direction dir : Plane.HORIZONTAL) {
			tryAddNeighbour(res, pos.offset(dir), dir.getOpposite(), true);
		}
		tryAddNeighbour(res, pos.offset(Direction.DOWN), Direction.UP, false);
		return res;
	}
	
	void tryAddNeighbour(List<BlockPos> to, BlockPos pos, Direction side, boolean stepUp) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof ICableConnected || (tile instanceof ICableSegment &&
				((ICableSegment) tile).canConnect(side))) {
			to.add(pos);
		} else if (stepUp) {
			tryAddNeighbour(to, pos.offset(Direction.UP), side, false);
			tryAddNeighbour(to, pos.offset(Direction.DOWN), side, false);
		}
	}
	
	@Override
	public boolean canConnect(Direction side) {
		return side != Direction.UP;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		connected = nbt.contains(tagConnection) ? BlockPos.fromLong(nbt.getLong(tagConnection)) : null;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		if (connected != null) nbt.putLong(tagConnection, connected.toLong());
		else nbt.remove(tagConnection);
		return nbt;
	}
	
}
