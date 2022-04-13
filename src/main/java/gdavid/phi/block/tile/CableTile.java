package gdavid.phi.block.tile;

import javax.annotation.Nullable;

import gdavid.phi.block.CableBlock;
import gdavid.phi.block.CableBlock.ConnectionState;
import gdavid.phi.util.ICableConnected;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class CableTile extends TileEntity implements ICableConnected {
	
	public static TileEntityType<CableTile> type;
	
	public static final String tagConnection = "connection";
	
	public @Nullable BlockPos connected = null;
	
	public CableTile() {
		super(type);
	}
	
	@Override
	public boolean connectsInDirection(Direction dir) {
		return Direction.Plane.HORIZONTAL.test(dir);
	}
	
	@Override
	public boolean connect(Direction side) {
		TileEntity tile = world.getTileEntity(pos.offset(side));
		if (tile instanceof ICableConnected) {
			ICableConnected con = (ICableConnected) tile;
			@Nullable BlockPos oc = con.getConnected(side.getOpposite());
			boolean did = connected != null && oc == null;
			if (connected == null && oc != null) {
				if (did = con.connect(side.getOpposite())) {
					connected = oc;
					markDirty();
				}
			}
			if (did) {
				world.setBlockState(pos, ((CableBlock) getBlockState().getBlock())
						.adjustConnections(getBlockState()
						.with(CableBlock.sides.get(side), ConnectionState.online)
						.with(CableBlock.online, connected != null), world, pos));
			}
			return did;
		}
		return false;
	}
	
	@Override
	public void disconnect(Direction side) {
	}
	
	@Override
	public @Nullable BlockPos getConnected(Direction dir) {
		return Direction.Plane.HORIZONTAL.test(dir) ? connected : null;
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
