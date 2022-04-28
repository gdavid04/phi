package gdavid.phi.network;

import gdavid.phi.block.tile.CADHolderTile;
import gdavid.phi.block.tile.CADHolderTile.ScanType;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CADScanMessage implements Message {
	
	final BlockPos pos;
	final ScanType type;
	
	public CADScanMessage(BlockPos pos, ScanType type) {
		this.pos = pos;
		this.type = type;
	}
	
	public CADScanMessage(PacketBuffer buf) {
		pos = buf.readBlockPos();
		type = buf.readEnumValue(ScanType.class);
	}
	
	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeEnumValue(type);
	}
	
	@Override
	@SuppressWarnings("resource")
	public boolean receive(Supplier<Context> context) {
		context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			TileEntity tile = Minecraft.getInstance().world.getTileEntity(pos);
			if (!(tile instanceof CADHolderTile)) return;
			((CADHolderTile) tile).setScanType(type);
		}));
		return true;
	}
	
}
