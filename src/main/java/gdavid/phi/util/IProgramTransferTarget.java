package gdavid.phi.util;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import vazkii.psi.api.spell.Spell;

public interface IProgramTransferTarget {
	
	BlockPos getPos();
	
	Spell getSpell();
	
	void setSpell(PlayerEntity player, Spell spell);
	
	default boolean hasSlots() {
		return false;
	}
	
	default List<Integer> getSlots() {
		return null;
	}
	
	default List<ResourceLocation> getSlotIcons() {
		return null;
	}
	
	default void selectSlot(int id) {
	}
	
}
