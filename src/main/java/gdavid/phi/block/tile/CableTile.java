package gdavid.phi.block.tile;

import gdavid.phi.block.CableBlock;
import gdavid.phi.block.CableBlock.CableSide;
import gdavid.phi.cable.ICableConnected;
import gdavid.phi.cable.ICableSegment;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.BlockPos;

public class CableTile extends BlockEntity implements ICableSegment {
	
	public static BlockEntityType<CableTile> type;
	
	public static final String tagConnection = "connection";
	
	public @Nullable BlockPos connected = null;
	
	public CableTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public BlockPos getConnection() {
		return connected;
	}
	
	@Override
	public void setConnection(@Nullable BlockPos connection, Predicate<BlockPos> connected) {
		this.connected = connection;
		setChanged();
		BlockState state = getBlockState();
		state = state.setValue(CableBlock.online, connection != null);
		for (Direction dir : Plane.HORIZONTAL) {
			CableSide side = CableSide.none;
			if (connected.test(worldPosition.relative(dir).relative(Direction.UP))) side = CableSide.up;
			else if (connected.test(worldPosition.relative(dir)) || connected.test(worldPosition.relative(dir).relative(Direction.DOWN)))
				side = CableSide.side;
			state = state.setValue(CableBlock.sides.get(dir), side);
		}
		level.setBlockAndUpdate(worldPosition, state);
	}
	
	@Override
	public Iterable<BlockPos> getNeighbours() {
		List<BlockPos> res = new ArrayList<>();
		for (Direction dir : Plane.HORIZONTAL) {
			tryAddNeighbour(res, worldPosition.relative(dir), dir.getOpposite(), true);
		}
		tryAddNeighbour(res, worldPosition.relative(Direction.DOWN), Direction.UP, false);
		return res;
	}
	
	void tryAddNeighbour(List<BlockPos> to, BlockPos pos, Direction side, boolean stepUp) {
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof ICableConnected
				|| (tile instanceof ICableSegment && ((ICableSegment) tile).canConnect(side))) {
			to.add(pos);
		} else if (stepUp) {
			tryAddNeighbour(to, pos.relative(Direction.UP), side, false);
			tryAddNeighbour(to, pos.relative(Direction.DOWN), side, false);
		}
	}
	
	@Override
	public boolean canConnect(Direction side) {
		return side != Direction.UP;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		connected = nbt.contains(tagConnection) ? BlockPos.of(nbt.getLong(tagConnection)) : null;
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (connected != null) nbt.putLong(tagConnection, connected.asLong());
		else nbt.remove(tagConnection);
	}
	
}
