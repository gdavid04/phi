package gdavid.phi.network;

import gdavid.phi.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import vazkii.psi.api.internal.Vector3;

import java.util.function.Supplier;

public class GravityMessage implements Message {
	
	final double x, y, z, power;
	final int duration;
	
	public GravityMessage(Vector3 vec, double power, int duration) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
		this.power = power;
		this.duration = duration;
	}
	
	public GravityMessage(FriendlyByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		power = buf.readDouble();
		duration = buf.readInt();
	}
	
	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeDouble(power);
		buf.writeInt(duration);
	}
	
	@Override
	public boolean receive(Supplier<Context> context) {
		context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			Minecraft.getInstance().player.getCapability(ModCapabilities.acceleration).ifPresent(cap -> {
				cap.addAccelerationTowardsPoint(new Vector3(x, y, z), power, duration);
			});
		}));
		return true;
	}
	
}
