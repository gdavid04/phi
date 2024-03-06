package gdavid.phi.block.tile;

import gdavid.phi.Phi;
import gdavid.phi.network.CADScanMessage;
import gdavid.phi.network.Messages;
import gdavid.phi.util.IProgramTransferTarget;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.item.ItemSpellDrive;

public class CADHolderTile extends BlockEntity implements IProgramTransferTarget {
	
	public static BlockEntityType<CADHolderTile> type;
	
	public static final String tagItem = "item";
	
	public ItemStack item = ItemStack.EMPTY;
	
	public ScanType scan = ScanType.none;
	public long scanTime;
	
	// Consider adding per-side selected slots and adding magazine compat
	
	public CADHolderTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public boolean hasItem() {
		return !item.isEmpty();
	}
	
	public void setItem(ItemStack stack) {
		item = stack;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 18);
	}
	
	public void removeItem() {
		setItem(ItemStack.EMPTY);
	}
	
	@Override
	public BlockPos getPosition() {
		return worldPosition;
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
	public void setSpell(Player player, Spell spell) {
		setScanType(ScanType.reprogram);
		if (item.getItem() instanceof ItemSpellDrive) {
			ItemSpellDrive.setSpell(item, spell);
			setChanged();
			return;
		}
		item.getCapability(PsiAPI.SPELL_ACCEPTOR_CAPABILITY).ifPresent(acceptor -> {
			acceptor.setSpell(player, spell);
			setChanged();
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
			setChanged();
		});
	}
	
	public void setScanType(ScanType type) {
		if (level.isClientSide) {
			if (scan.ordinal() > type.ordinal()) return;
			if (scan == ScanType.none) {
				scanTime = System.currentTimeMillis();
			} else if (System.currentTimeMillis() > scanTime + 1000) {
				scanTime = 2 * System.currentTimeMillis() - scanTime - 2000;
			}
			scan = type;
		} else {
			CADScanMessage message = new CADScanMessage(worldPosition, type);
			Messages.channel.send(PacketDistributor.NEAR
					.with(TargetPoint.p(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 64, level.dimension())), message);
		}
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		item = ItemStack.of(nbt.getCompound(tagItem));
	}
	
	@Override
	public CompoundTag serializeNBT() {
		var nbt = super.serializeNBT();
		nbt.put(tagItem, item.save(new CompoundTag()));
		return nbt;
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this, IForgeBlockEntity::serializeNBT);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		return serializeNBT();
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		load(packet.getTag());
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
