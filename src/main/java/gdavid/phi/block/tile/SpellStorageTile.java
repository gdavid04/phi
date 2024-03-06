package gdavid.phi.block.tile;

import gdavid.phi.Phi;
import gdavid.phi.util.IProgramTransferTarget;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import vazkii.psi.api.spell.Spell;

public class SpellStorageTile extends BlockEntity implements IProgramTransferTarget {
	
	public static BlockEntityType<SpellStorageTile> type;
	
	public static final String tagSpell = "spell_";
	public static final String tagSelectedSlot = "selected_slot";
	
	public static final int slots = 15;
	
	public Spell[] spells = new Spell[slots];
	
	public int selectedSlot = 0;
	
	public SpellStorageTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public BlockPos getPosition() {
		return worldPosition;
	}
	
	@Override
	public Spell getSpell() {
		return spells[selectedSlot];
	}
	
	@Override
	public void setSpell(Player player, Spell spell) {
		spells[selectedSlot] = spell;
		setChanged();
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
		setChanged();
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		for (int i = 0; i < slots; i++) {
			spells[i] = Spell.createFromNBT(nbt.getCompound(tagSpell + i));
		}
		selectedSlot = nbt.getInt(tagSelectedSlot);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		var nbt = super.serializeNBT();
		for (int i = 0; i < slots; i++) {
			CompoundTag spellNbt = new CompoundTag();
			if (spells[i] != null) spells[i].writeToNBT(spellNbt);
			nbt.put(tagSpell + i, spellNbt);
		}
		nbt.putInt(tagSelectedSlot, selectedSlot);
		return nbt;
	}
	
}
