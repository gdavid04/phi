package gdavid.phi.block.tile;

import gdavid.phi.Phi;
import gdavid.phi.network.CADScanMessage;
import gdavid.phi.network.Messages;
import gdavid.phi.util.IProgramTransferTarget;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.item.ItemSpellDrive;

public class CADHolderTile extends TileEntity implements IProgramTransferTarget {
	
	public static TileEntityType<CADHolderTile> type;
	
	public static final String tagItem = "item";
	
	public ItemStack item = ItemStack.EMPTY;
	
	public ScanType scan = ScanType.none;
	public long scanTime;
	
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
	
	@Override
	public Spell getSpell() {
		setScanType(ScanType.scan);
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
	
	@Override
	public void setSpell(PlayerEntity player, Spell spell) {
		setScanType(ScanType.reprogram);
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
	public boolean hasSlots() {
		return item.getCapability(PsiAPI.SOCKETABLE_CAPABILITY).isPresent();
	}
	
	@Override
	public List<Integer> getSlots() {
		ISocketable socketable = item.getCapability(PsiAPI.SOCKETABLE_CAPABILITY).orElse(null);
		if (socketable == null) return null;
		return socketable.getRadialMenuSlots();
	}
	
	@Override
	public List<ResourceLocation> getSlotIcons() {
		ISocketable socketable = item.getCapability(PsiAPI.SOCKETABLE_CAPABILITY).orElse(null);
		if (socketable == null) return null;
		return socketable.getRadialMenuIcons();
	}
	
	@Override
	public void selectSlot(int id) {
		item.getCapability(PsiAPI.SOCKETABLE_CAPABILITY).ifPresent(socketable -> {
			socketable.setSelectedSlot(id);
			markDirty();
		});
	}
	
	public void setScanType(ScanType type) {
		if (world.isRemote) {
			if (scan.ordinal() > type.ordinal()) return;
			if (scan == ScanType.none) {
				scanTime = System.currentTimeMillis();
			} else if (System.currentTimeMillis() > scanTime + 1000) {
				scanTime = 2 * System.currentTimeMillis() - scanTime - 2000;
			}
			scan = type;
		} else {
			CADScanMessage message = new CADScanMessage(pos, type);
			Messages.channel.send(PacketDistributor.NEAR
					.with(TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 64, world.getDimensionKey())), message);
		}
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
	
	public enum ScanType {
		
		none(null), scan(new ResourceLocation(Phi.modId, "textures/block/cad_holder_scan.png")),
		reprogram(new ResourceLocation(Phi.modId, "textures/block/cad_holder_reprogram.png"));
		
		public final ResourceLocation texture;
		
		ScanType(ResourceLocation texture) {
			this.texture = texture;
		}
		
	}
	
}
