package gdavid.phi.capability;

import gdavid.phi.item.SpellMagazineItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.ISpellAcceptor;

public class MagazineSocketable implements ICapabilityProvider, ISocketable {
	
	final ItemStack item;
	
	public int slots;
	
	public MagazineSocketable(ItemStack item, int slots) {
		this.item = item;
		this.slots = slots;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
		if (capability == PsiAPI.SOCKETABLE_CAPABILITY) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();
	}
	
	@Override
	public boolean isSocketSlotAvailable(int slot) {
		return slot < slots;
	}
	
	@Override
	public ItemStack getBulletInSocket(int slot) {
		CompoundTag nbt = item.getOrCreateTagElement(SpellMagazineItem.tagSlot).getCompound(Integer.toString(slot));
		if (nbt.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return ItemStack.of(nbt);
	}
	
	@Override
	public void setBulletInSocket(int slot, ItemStack bullet) {
		CompoundTag nbt = new CompoundTag();
		if (!bullet.isEmpty()) {
			bullet.save(nbt);
		}
		item.getOrCreateTagElement(SpellMagazineItem.tagSlot).put(Integer.toString(slot), nbt);
	}
	
	// TODO consider allowing selecting slots and using programmer
	
	@Override
	public int getSelectedSlot() {
		// not applicable
		return 0;
	}
	
	@Override
	public void setSelectedSlot(int slot) {
		// not applicable
	}
	
	@Override
	public int getLastSlot() {
		return slots - 1;
	}
	
	@Override
	public boolean isItemValid(int slot, ItemStack bullet) {
		return isSocketSlotAvailable(slot) && ISpellAcceptor.isContainer(bullet);
	}
	
}
