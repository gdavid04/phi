package gdavid.phi.network;

import java.util.function.Supplier;

import gdavid.phi.util.IProgramTransferTarget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ProgramTransferSlotMessage implements Message {
	
	final BlockPos pos;
	final int slot;
	
	public ProgramTransferSlotMessage(BlockPos pos, int slot) {
		this.pos = pos;
		this.slot = slot;
	}
	
	public ProgramTransferSlotMessage(PacketBuffer buf) {
		pos = buf.readBlockPos();
		slot = buf.readInt();
	}
	
	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(slot);
	}
	
	@Override
	public boolean receive(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			PlayerEntity player = context.get().getSender();
			TileEntity tile = player.world.getTileEntity(pos);
			if (tile instanceof IProgramTransferTarget) {
				((IProgramTransferTarget) tile).selectSlot(slot);
			}
		});
		return true;
	}
	
}
