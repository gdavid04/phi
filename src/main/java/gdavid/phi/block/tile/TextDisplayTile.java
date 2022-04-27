package gdavid.phi.block.tile;

import gdavid.phi.cable.ICableConnected;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

public class TextDisplayTile extends TileEntity implements ICableConnected {
	
	public static TileEntityType<TextDisplayTile> type;
	
	public static final String tagText = "text";
	
	public static final int lines = 16, columns = 32;
	
	public List<String> text = new ArrayList<>();
	
	public TextDisplayTile() {
		super(type);
	}
	
	public void appendLine(String line) {
		do {
			String prefix;
			int eol = line.indexOf('\n');
			if (eol != -1) {
				prefix = line.substring(0, eol);
				line = line.substring(eol + 1);
			} else {
				prefix = line;
				line = "";
			}
			for (int i = 0; i < prefix.length() / columns; i++) {
				text.add(prefix.substring(i * columns, (i + 1) * columns));
				if (text.size() > lines) text.remove(0);
			}
			text.add(prefix.substring(prefix.length() / columns * columns));
			if (text.size() > lines) text.remove(0);
		} while (!line.isEmpty());
		markDirty();
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
	}
	
	public void setLine(String line, int index) {
		if (index < 1 || index > lines) return;
		while (index > text.size()) text.add("");
		do {
			String prefix;
			int eol = line.indexOf('\n');
			if (eol != -1) {
				prefix = line.substring(0, eol);
				line = line.substring(eol + 1);
			} else {
				prefix = line;
				line = "";
			}
			for (int i = 0; i < prefix.length() / columns; i++) {
				if (index > text.size()) text.add("");
				text.set(index++ - 1, prefix.substring(i * columns, (i + 1) * columns));
				if (index > lines) return;
			}
			if (index > text.size()) text.add("");
			text.set(index++ - 1, prefix.substring(prefix.length() / columns * columns));
			if (index > lines) return;
		} while (!line.isEmpty());
		markDirty();
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		read(nbt);
	}
	
	public void read(CompoundNBT nbt) {
		text = new ArrayList<>();
		for (INBT line : nbt.getList(tagText, Constants.NBT.TAG_STRING)) {
			text.add(line.getString());
		}
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		ListNBT list = new ListNBT();
		for (String line : text) {
			list.add(StringNBT.valueOf(line));
		}
		nbt.put(tagText, list);
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
	
	@Override
	public boolean isController() {
		return true;
	}
	
}
