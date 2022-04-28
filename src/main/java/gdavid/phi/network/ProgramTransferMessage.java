package gdavid.phi.network;

import java.util.function.Supplier;

import gdavid.phi.util.IProgramTransferTarget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import vazkii.psi.api.internal.VanillaPacketDispatcher;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.block.tile.TileProgrammer;

public class ProgramTransferMessage implements Message {
	
	final BlockPos pos;
	final Direction dir;
	
	public ProgramTransferMessage(BlockPos pos, Direction dir) {
		this.pos = pos;
		this.dir = dir;
	}
	
	public ProgramTransferMessage(PacketBuffer buf) {
		pos = buf.readBlockPos();
		dir = Direction.byIndex(buf.readInt());
	}
	
	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(dir.getIndex());
	}
	
	@Override
	public boolean receive(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			PlayerEntity player = context.get().getSender();
			TileEntity a = player.world.getTileEntity(pos);
			TileEntity b = player.world.getTileEntity(pos.offset(dir));
			Spell spell;
			if (a instanceof TileProgrammer) {
				spell = ((TileProgrammer) a).spell.copy();
			} else if (a instanceof IProgramTransferTarget) {
				spell = ((IProgramTransferTarget) a).getSpell();
			} else return;
			if (b instanceof TileProgrammer) {
				((TileProgrammer) b).spell = spell;
				((TileProgrammer) b).onSpellChanged();
				VanillaPacketDispatcher.dispatchTEToNearbyPlayers(b);
			} else if (b instanceof IProgramTransferTarget) {
				((IProgramTransferTarget) b).setSpell(player, spell);
			}
		});
		return true;
	}
	
}
