package gdavid.phi.network;

import gdavid.phi.util.IProgramTransferTarget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkEvent.Context;
import vazkii.psi.api.internal.VanillaPacketDispatcher;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.block.tile.TileProgrammer;

import java.util.function.Supplier;

public class ProgramTransferMessage implements Message {
	
	final BlockPos pos;
	final Direction dir;
	
	public ProgramTransferMessage(BlockPos pos, Direction dir) {
		this.pos = pos;
		this.dir = dir;
	}
	
	public ProgramTransferMessage(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		dir = Direction.from3DDataValue(buf.readInt());
	}
	
	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(dir.get3DDataValue());
	}
	
	@Override
	public boolean receive(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			Player player = context.get().getSender();
			BlockEntity a = player.level.getBlockEntity(pos);
			BlockEntity b = player.level.getBlockEntity(pos.relative(dir));
			Spell spell;
			if (a instanceof TileProgrammer) {
				Spell tmp = ((TileProgrammer) a).spell;
				if (tmp != null) spell = tmp.copy();
				else spell = null;
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
