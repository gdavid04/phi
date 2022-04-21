package gdavid.phi.block.tile;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class CADHolderTile extends TileEntity {
	
	public static TileEntityType<CADHolderTile> type;
	
	public static final String tagItem = "item";
	
	public ItemStack item = ItemStack.EMPTY; // Do not access directly outside renderer
	
	public CADHolderTile() {
		super(type);
	}
	
	public boolean hasItem() {
		return !item.isEmpty();
	}
	
	public ItemStack getItem() {
		return item.copy();
	}
	
	public void setItem(ItemStack stack) {
		item = stack.copy();
		markDirty();
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
	}
	
	public void removeItem() {
		setItem(ItemStack.EMPTY);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		read(nbt);
	}
	
	public void read(CompoundNBT nbt) {
		item = ItemStack.read(nbt.getCompound(tagItem));
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.put(tagItem, item.write(new CompoundNBT()));
		return nbt;
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getPos(), 0, write(new CompoundNBT()));
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		read(packet.getNbtCompound());
	}
	
}
