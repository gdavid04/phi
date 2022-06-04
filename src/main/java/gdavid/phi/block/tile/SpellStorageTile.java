package gdavid.phi.block.tile;

import gdavid.phi.Phi;
import gdavid.phi.util.IProgramTransferTarget;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import vazkii.psi.api.spell.Spell;

public class SpellStorageTile extends TileEntity implements IProgramTransferTarget {
	
	public static TileEntityType<SpellStorageTile> type;
	
	public static final String tagSpell = "spell_";
	public static final String tagSelectedSlot = "selected_slot";
	
	public static final int slots = 15;
	
	public Spell[] spells = new Spell[slots];
	
	public int selectedSlot = 0;
	
	public SpellStorageTile() {
		super(type);
	}
	
	@Override
	public BlockPos getPosition() {
		return pos;
	}
	
	@Override
	public Spell getSpell() {
		return spells[selectedSlot];
	}
	
	@Override
	public void setSpell(PlayerEntity player, Spell spell) {
		spells[selectedSlot] = spell;
		markDirty();
	}
	
	@Override
	public boolean hasSlots() {
		return true;
	}
	
	@Override
	public List<Integer> getSlots() {
		return Stream.iterate(0, x -> x + 1).limit(slots).collect(Collectors.toList());
	}
	
	@Override
	public List<ResourceLocation> getSlotIcons() {
		return Stream.iterate(0, x -> x + 1).limit(slots)
				.map(x -> new ResourceLocation(Phi.modId, "textures/gui/signs/spell_storage_" + x + ".png"))
				.collect(Collectors.toList());
	}
	
	@Override
	public void selectSlot(int id) {
		if (id < 0 || id >= slots) return;
		selectedSlot = id;
		markDirty();
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		read(nbt);
	}
	
	public void read(CompoundNBT nbt) {
		for (int i = 0; i < slots; i++) {
			spells[i] = Spell.createFromNBT(nbt.getCompound(tagSpell + i));
		}
		selectedSlot = nbt.getInt(tagSelectedSlot);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		for (int i = 0; i < slots; i++) {
			CompoundNBT spellNbt = new CompoundNBT();
			if (spells[i] != null) spells[i].writeToNBT(spellNbt);
			nbt.put(tagSpell + i, spellNbt);
		}
		nbt.putInt(tagSelectedSlot, selectedSlot);
		return nbt;
	}
	
}
