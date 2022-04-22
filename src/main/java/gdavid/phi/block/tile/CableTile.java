package gdavid.phi.block.tile;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public class CableTile extends TileEntity {
	
	public static TileEntityType<CableTile> type;
	
	public static final String tagConnection = "connection";
	
	public @Nullable BlockPos connected = null;
	
	public CableTile() {
		super(type);
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
