package gdavid.phi.block.tile;

import gdavid.phi.cable.ICableConnected;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TextSUTile extends BlockEntity implements ICableConnected {
	
	public static BlockEntityType<TextSUTile> type;
	
	public static final String tagText = "text";
	
	private String text = "";
	
	public TextSUTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String value) {
		text = value;
		if (text.length() > getCapacity()) text = text.substring(0, getCapacity());
		setChanged();
	}
	
	public int getCapacity() {
		return 256;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		text = nbt.getString(tagText);
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putString(tagText, text);
	}
	
	@Override
	public boolean isController() {
		return true;
	}
	
}
