package gdavid.phi.block.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.item.ItemSpellDrive;

public class CADHolderTile extends TileEntity {
	
	public static TileEntityType<CADHolderTile> type;
	
	public static final String tagItem = "item";
	
	public ItemStack item = ItemStack.EMPTY;
	
	public CADHolderTile() {
		super(type);
	}
	
	public boolean hasItem() {
		return !item.isEmpty();
	}
	
	public void setItem(ItemStack stack) {
		item = stack;
		markDirty();
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
	}
	
	public void removeItem() {
		setItem(ItemStack.EMPTY);
	}
	
	public Spell getSpell() {
		if (item.getItem() instanceof ItemSpellDrive) return ItemSpellDrive.getSpell(item);
		ISpellAcceptor acceptor = item.getCapability(PsiAPI.SPELL_ACCEPTOR_CAPABILITY).orElse(null);
		Spell spell = acceptor == null ? null : acceptor.getSpell();
		if (spell == null) {
			ISocketable socketable = item.getCapability(PsiAPI.SOCKETABLE_CAPABILITY).orElse(null);
			if (socketable != null) {
				acceptor = socketable.getSelectedBullet().getCapability(PsiAPI.SPELL_ACCEPTOR_CAPABILITY).orElse(null);
				spell = acceptor == null ? null : acceptor.getSpell();
			}
		}
		return spell;
	}
	
	public void setSpell(PlayerEntity player, Spell spell) {
		if (item.getItem() instanceof ItemSpellDrive) {
			ItemSpellDrive.setSpell(item, spell);
			markDirty();
			return;
		}
		item.getCapability(PsiAPI.SPELL_ACCEPTOR_CAPABILITY).ifPresent(acceptor -> {
			acceptor.setSpell(player, spell);
			markDirty();
		});
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		read(nbt);
	}
	
	public void read(CompoundNBT nbt) {
		item = ItemStack.read(nbt.getCompound(tagItem));
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.put(tagItem, item.write(new CompoundNBT()));
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
	
}
