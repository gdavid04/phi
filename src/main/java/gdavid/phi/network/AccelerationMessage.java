package gdavid.phi.network;

import java.util.function.Supplier;

import gdavid.phi.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import vazkii.psi.api.internal.Vector3;

public class AccelerationMessage implements Message {
	
	final double x, y, z;
	final int duration;
	
	public AccelerationMessage(Vector3 vec, int duration) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
		this.duration = duration;
	}
	
	public AccelerationMessage(PacketBuffer buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		duration = buf.readInt();
	}
	
	@Override
	public void encode(PacketBuffer buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeInt(duration);
	}
	
	@Override
	@SuppressWarnings("resource")
	public boolean receive(Supplier<Context> context) {
		context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			Minecraft.getInstance().player.getCapability(ModCapabilities.acceleration).ifPresent(cap -> {
				cap.addAcceleration(new Vector3(x, y, z), duration);
			});
		}));
		return true;
	}
	
}
