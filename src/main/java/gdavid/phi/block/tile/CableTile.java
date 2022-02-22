package gdavid.phi.block.tile;

import java.util.Objects;

import gdavid.phi.util.ICableConnected;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

public class CableTile extends TileEntity implements ICableConnected {
	
	public static TileEntityType<CableTile> type;
	
	public static final String tagConnection = "connection";
	
	public Connection connection = null;
	
	public CableTile() {
		super(type);
	}
	
	public void updateConnection() {
		Connection best = null;
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			TileEntity tile = world.getTileEntity(pos.offset(dir));
			if (tile instanceof ICableConnected) {
				Connection connection = ((ICableConnected) tile).getController(dir.getOpposite());
				if (connection == null || connection.side == dir.getOpposite()) continue;
				if (best == null || connection.distance + 1 < best.distance) {
					best = new Connection(connection.pos, dir, connection.distance + 1);
					if (best.distance == 0) break;
				}
			}
		}
		if (!Objects.equals(connection, best)) {
			connection = best;
			markDirty();
		}
	}
	
	@Override
	public Connection getController(Direction side) {
		return Direction.Plane.HORIZONTAL.test(side) ? connection : null;
	}
	
	@Override
	public boolean isAcceptor(Direction side) {
		return false;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		connection = Connection.read(nbt.getCompound(tagConnection));
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.put(tagConnection, Connection.write(connection));
		return nbt;
	}
	
}
