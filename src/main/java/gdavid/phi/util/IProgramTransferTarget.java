package gdavid.phi.util;

import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import vazkii.psi.api.spell.Spell;

public interface IProgramTransferTarget {
	
	BlockPos getPosition();
	
	Spell getSpell();
	
	void setSpell(Player player, Spell spell);
	
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
