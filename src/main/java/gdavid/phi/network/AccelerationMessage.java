package gdavid.phi.network;

import gdavid.phi.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import vazkii.psi.api.internal.Vector3;

import java.util.function.Supplier;

public class AccelerationMessage implements Message {
	
	final double x, y, z;
	final int duration;
	
	public AccelerationMessage(Vector3 vec, int duration) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
		this.duration = duration;
	}
	
	public AccelerationMessage(FriendlyByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		duration = buf.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeInt(duration);
	}
	
	@Override
	public boolean receive(Supplier<Context> context) {
		context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			Minecraft.getInstance().player.getCapability(ModCapabilities.acceleration).ifPresent(cap -> {
				cap.addAcceleration(new Vector3(x, y, z), duration);
			});
		}));
		return true;
	}
	
}
