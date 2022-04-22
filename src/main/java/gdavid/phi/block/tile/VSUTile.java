package gdavid.phi.block.tile;

import gdavid.phi.util.CableNetwork.ICableConnected;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;
import vazkii.psi.api.internal.Vector3;

public class VSUTile extends TileEntity implements ICableConnected {
	
	public static TileEntityType<VSUTile> type;
	
	public static final String tagVector = "vector";
	
	private Vector3 vector = Vector3.zero;
	
	public VSUTile() {
		super(type);
	}
	
	public Vector3 getVector() {
		return vector.copy();
	}
	
	public void setVector(Vector3 value) {
		vector = value;
		markDirty();
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		ListNBT list = nbt.getList(tagVector, Constants.NBT.TAG_DOUBLE);
		vector = new Vector3(list.getDouble(0), list.getDouble(1), list.getDouble(2));
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		ListNBT list = new ListNBT();
		list.add(DoubleNBT.valueOf(vector.x));
		list.add(DoubleNBT.valueOf(vector.y));
		list.add(DoubleNBT.valueOf(vector.z));
		nbt.put(tagVector, list);
		return nbt;
	}
	
}
