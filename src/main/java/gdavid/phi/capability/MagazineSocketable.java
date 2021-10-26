package gdavid.phi.capability;

import gdavid.phi.item.SpellMagazineItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
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
		CompoundNBT nbt = item.getOrCreateChildTag(SpellMagazineItem.tagSlot + slot);
		if (nbt.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return ItemStack.read(nbt);
	}
	
	@Override
	public void setBulletInSocket(int slot, ItemStack bullet) {
		CompoundNBT nbt = new CompoundNBT();
		if (!bullet.isEmpty()) {
			bullet.write(nbt);
		}
		item.getOrCreateTag().put(SpellMagazineItem.tagSlot + slot, nbt);
	}
	
	// TODO allow selecting slots and using programmer
	
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
