package gdavid.phi.capability;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICADData;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.internal.Vector3;

public class MPUCADData implements ICapabilityProvider, ICADData, ISocketable {
	
	public static final String tagTime = "time";
	public static final String tagMemory = "memory";
	
	public final ItemStack stack;
	
	public int time;
	public List<Vector3> vectors = new ArrayList<>();
	
	private boolean dirty;
	
	public MPUCADData(ItemStack stack) {
		this.stack = stack;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == PsiAPI.CAD_DATA_CAPABILITY || cap == PsiAPI.SOCKETABLE_CAPABILITY) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt(tagTime, time);
		ListNBT memory = new ListNBT();
		for (Vector3 vec : vectors) {
			memory.add(DoubleNBT.valueOf(vec.x));
			memory.add(DoubleNBT.valueOf(vec.y));
			memory.add(DoubleNBT.valueOf(vec.z));
		}
		nbt.put(tagMemory, memory);
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		time = nbt.getInt(tagTime);
		ListNBT memory = nbt.getList(tagMemory, Constants.NBT.TAG_DOUBLE);
		for (int i = 0; i < memory.size() / 3; i++) {
			vectors.add(new Vector3(memory.getDouble(i * 3), memory.getDouble(i * 3 + 1), memory.getDouble(i * 3 + 2)));
		}
	}
	
	@Override
	public boolean isSocketSlotAvailable(int slot) {
		return false;
	}
	
	@Override
	public ItemStack getBulletInSocket(int slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setBulletInSocket(int slot, ItemStack bullet) {
	}
	
	@Override
	public int getSelectedSlot() {
		return 0;
	}
	
	@Override
	public void setSelectedSlot(int slot) {
	}
	
	@Override
	public int getTime() {
		return time;
	}
	
	@Override
	public void setTime(int time) {
		this.time = time;
	}
	
	@Override
	public int getBattery() {
		return 0;
	}
	
	@Override
	public void setBattery(int battery) {
	}
	
	@Override
	public Vector3 getSavedVector(int memorySlot) {
		if (memorySlot < 0 || memorySlot >= vectors.size()) return Vector3.zero;
		return vectors.get(memorySlot);
	}
	
	@Override
	public void setSavedVector(int memorySlot, Vector3 value) {
		while (vectors.size() <= memorySlot)
			vectors.add(Vector3.zero);
		vectors.set(memorySlot, value);
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public void markDirty(boolean isDirty) {
		dirty = isDirty;
	}
	
	@Override
	public CompoundNBT serializeForSynchronization() {
		return null;
	}
	
}
