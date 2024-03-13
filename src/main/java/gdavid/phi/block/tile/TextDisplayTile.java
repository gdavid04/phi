package gdavid.phi.block.tile;

import gdavid.phi.cable.ICableConnected;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeBlockEntity;

public class TextDisplayTile extends BlockEntity implements ICableConnected {
	
	public static BlockEntityType<TextDisplayTile> type;
	
	public static final String tagText = "text";
	
	public static final int lines = 16, columns = 32;
	
	public List<String> text = new ArrayList<>();
	
	// TODO formatting code support
	
	public TextDisplayTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
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
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 18);
	}
	
	public void setLine(String line, int index) {
		if (index < 1 || index > lines) return;
		while (index > text.size())
			text.add("");
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
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 18);
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		text = new ArrayList<>();
		for (Tag line : nbt.getList(tagText, CompoundTag.TAG_STRING)) {
			text.add(line.getAsString());
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		var list = new ListTag();
		for (String line : text) {
			list.add(StringTag.valueOf(line));
		}
		nbt.put(tagText, list);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		var nbt = new CompoundTag();
		saveAdditional(nbt);
		return nbt;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		load(packet.getTag());
	}
	
	@Override
	public boolean isController() {
		return true;
	}
	
}
