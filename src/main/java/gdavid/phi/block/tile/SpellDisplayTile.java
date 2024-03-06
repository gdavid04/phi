package gdavid.phi.block.tile;

import java.util.UUID;

import gdavid.phi.util.IProgramTransferTarget;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import vazkii.psi.api.spell.Spell;

public class SpellDisplayTile extends BlockEntity implements IProgramTransferTarget {
	
	public static BlockEntityType<SpellDisplayTile> type;
	
	public static final String tagSpell = "spell";
	
	public Spell spell;
	
	public SpellDisplayTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public BlockPos getPosition() {
		return worldPosition;
	}
	
	@Override
	public Spell getSpell() {
		return spell;
	}
	
	@Override
	public void setSpell(Player player, Spell spell) {
		setSpell(spell);
	}
	
	public void setSpell(Spell to) {
		if (to == null) {
			spell = null;
		} else {
			spell = to.copy();
			spell.uuid = UUID.randomUUID();
		}
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 18);
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (spell == null) spell = Spell.createFromNBT(nbt.getCompound(tagSpell));
		else spell.readFromNBT(nbt.getCompound(tagSpell));
	}
	
	@Override
	public CompoundTag serializeNBT() {
		var nbt = super.serializeNBT();
		CompoundTag spellNbt = new CompoundTag();
		if (spell != null) spell.writeToNBT(spellNbt);
		nbt.put(tagSpell, spellNbt);
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
	
}
