package gdavid.phi.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.item.ItemSpellDrive;

public class SimpleSpellAcceptor implements ICapabilityProvider, ISpellAcceptor {
	
	final ItemStack item;
	
	public SimpleSpellAcceptor(ItemStack item) {
		this.item = item;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction dir) {
		return PsiAPI.SPELL_ACCEPTOR_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> this));
	}
	
	@Override
	public boolean containsSpell() {
		return item.getOrCreateTag().getBoolean("has_spell");
	}
	
	@Override
	public Spell getSpell() {
		return ItemSpellDrive.getSpell(item);
	}
	
	@Override
	public void setSpell(PlayerEntity player, Spell spell) {
		ItemSpellDrive.setSpell(item, spell);
	}
	
}
