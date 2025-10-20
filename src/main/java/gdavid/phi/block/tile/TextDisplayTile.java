package gdavid.phi.block.tile;

import gdavid.phi.cable.ICableConnected;
import java.util.ArrayList;
import java.util.Arrays;
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

public class TextDisplayTile extends BlockEntity implements ICableConnected {
	
	public static BlockEntityType<TextDisplayTile> type;
	
	public static final String tagText = "text";
	
	public static final int lines = 16, columns = 32;
	
	public List<String> text = new ArrayList<>();
	
	public TextDisplayTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public void appendLine(String line) {
		for (var actualLine : normalizeFormatting(line)) {
			text.add(actualLine);
			if (text.size() > lines) text.remove(0);
		}
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 18);
	}
	
	public void setLine(String line, int index) {
		if (index < 1 || index > lines) return;
		while (index > text.size()) text.add("");
		for (var actualLine : normalizeFormatting(line)) {
			if (index > text.size()) text.add("");
			text.set(index++ - 1, actualLine);
			if (index > lines) return;
		}
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 18);
	}
	
	/**
	 * Remove redundant formatting codes and insert line breaks
	 * every {@link #columns} characters.
	 * This prevents arbitrary length strings from being inserted
	 * as formatting codes bypass columns limit and handles
	 * line wrapping formatted text
	 */
	private List<String> normalizeFormatting(String str) {
		var res = new ArrayList<String>();
		var b = new StringBuilder();
		int column = 0;
		
		char curColor = 0;
		boolean[] curFormats = new boolean[5];
		
		int i = 0;
		while (i < str.length()) {
			char color = 0;
			boolean[] formats = new boolean[5];
			boolean reset = false;
			
			while (i + 1 < str.length() && str.charAt(i) == '\u00a7') {
				char code = Character.toLowerCase(str.charAt(i + 1));
				if (code == 'r') {
					reset = true;
					curColor = color = 0;
					Arrays.fill(formats, false);
					Arrays.fill(curFormats, false);
				} else if ((code >= '0' && code <= '9') || (code >= 'a' && code <= 'f')) {
					curColor = color = code;
					// Color codes reset formatting
					Arrays.fill(formats, false);
					Arrays.fill(curFormats, false);
				} else if (code >= 'k' && code <= 'o') curFormats[code - 'k'] = formats[code - 'k'] = true;
				else break; // Unknown formatting code, append as text
				i += 2;
			}
			
			if (reset) b.append("\u00a7r");
			if (color != 0) b.append('\u00a7').append(color);
			for (int f = 0; f < 5; f++) {
				if (formats[f]) b.append('\u00a7').append((char) ('k' + f));
			}
			
			int from = i;
			i = str.indexOf('\u00a7', from);
			if (i == -1) i = str.length();
			int nl = str.indexOf('\n', from);
			if (nl != -1 && nl < i) i = nl;
			if (column + i - from > columns) i = from + columns - column;
			column += i - from;
			b.append(str, from, i);
			if (column >= columns || nl == i) {
				if (nl == i) i++;
				column = 0;
				res.add(b.toString());
				b = new StringBuilder();
				
				// Keep formatting on wrap
				if (curColor != 0) b.append('\u00a7').append(curColor);
				for (int f = 0; f < 5; f++) {
					if (curFormats[f]) b.append('\u00a7').append((char) ('k' + f));
				}
			}
		}
		res.add(b.toString());
		return res;
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
