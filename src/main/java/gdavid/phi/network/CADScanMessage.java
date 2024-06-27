package gdavid.phi.network;

import gdavid.phi.block.tile.CADHolderTile;
import gdavid.phi.block.tile.CADHolderTile.ScanType;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class CADScanMessage implements Message {
	
	final BlockPos pos;
	final ScanType type;
	
	public CADScanMessage(BlockPos pos, ScanType type) {
		this.pos = pos;
		this.type = type;
	}
	
	public CADScanMessage(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		type = buf.readEnum(ScanType.class);
	}
	
	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(type);
	}
	
	@Override
	public boolean receive(Supplier<Context> context) {
		context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
			if (!(tile instanceof CADHolderTile)) return;
			((CADHolderTile) tile).setScanType(type);
		}));
		return true;
	}
	
}
