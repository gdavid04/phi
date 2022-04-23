package gdavid.phi.block.tile;

import gdavid.phi.cable.ICableConnected;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TextSUTile extends TileEntity implements ICableConnected {
	
	public static TileEntityType<TextSUTile> type;
	
	public static final String tagText = "text";
	
	private String text = "";
	
	public TextSUTile() {
		super(type);
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String value) {
		text = value;
		if (text.length() > getCapacity()) text = text.substring(0, getCapacity());
		markDirty();
	}
	
	public int getCapacity() {
		return 256;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		text = nbt.getString(tagText);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.putString(tagText, text);
		return nbt;
	}

	@Override
	public boolean isController() {
		return true;
	}
	
}
