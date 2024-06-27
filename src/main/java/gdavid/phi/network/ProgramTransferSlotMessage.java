package gdavid.phi.network;

import gdavid.phi.util.IProgramTransferTarget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class ProgramTransferSlotMessage implements Message {
	
	final BlockPos pos;
	final int slot;
	
	public ProgramTransferSlotMessage(BlockPos pos, int slot) {
		this.pos = pos;
		this.slot = slot;
	}
	
	public ProgramTransferSlotMessage(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		slot = buf.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(slot);
	}
	
	@Override
	public boolean receive(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			Player player = context.get().getSender();
			BlockEntity tile = player.level.getBlockEntity(pos);
			if (tile instanceof IProgramTransferTarget) {
				((IProgramTransferTarget) tile).selectSlot(slot);
			}
		});
		return true;
	}
	
}
