package gdavid.phi.block.tile;

import gdavid.phi.cable.ICableConnected;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vazkii.psi.api.internal.Vector3;

public class VSUTile extends BlockEntity implements ICableConnected {
	
	public static BlockEntityType<VSUTile> type;
	
	public static final String tagVector = "vector";
	
	private Vector3 vector = Vector3.zero;
	
	public VSUTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public Vector3 getVector() {
		return vector.copy();
	}
	
	public void setVector(Vector3 value) {
		vector = value;
		setChanged();
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		ListTag list = nbt.getList(tagVector, CompoundTag.TAG_DOUBLE);
		vector = new Vector3(list.getDouble(0), list.getDouble(1), list.getDouble(2));
	}
	
	@Override
	public CompoundTag serializeNBT() {
		var nbt = super.serializeNBT();
		ListTag list = new ListTag();
		list.add(DoubleTag.valueOf(vector.x));
		list.add(DoubleTag.valueOf(vector.y));
		list.add(DoubleTag.valueOf(vector.z));
		nbt.put(tagVector, list);
		return nbt;
	}
	
	@Override
	public boolean isController() {
		return true;
	}
	
}
