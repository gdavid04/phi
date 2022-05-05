package gdavid.phi.capability;

import gdavid.phi.Phi;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.psi.api.internal.Vector3;

@EventBusSubscriber
public class AccelerationCapability implements IAccelerationCapability, INBTSerializable<CompoundNBT> {
	
	static final String tagAccelerations = "accelerations";
	static final String tagGravities = "gravities";
	static final String tagPower = "power";
	static final String tagDuration = "duration";
	
	List<Acceleration> accelerations = new ArrayList<>();
	List<Gravity> gravities = new ArrayList<>();
	
	@Override
	public Vector3 getAcceleration(Entity entity) {
		Vector3 res = new Vector3();
		for (Acceleration a : accelerations) {
			res.add(a.value);
		}
		for (Gravity g : gravities) {
			Vector3 diff = g.center.copy().sub(Vector3.fromEntity(entity));
			double mag = diff.mag();
			res.add(diff.normalize().multiply(Math.min(g.power, mag)));
		}
		return res;
	}
	
	@Override
	public void addAcceleration(Vector3 acceleration, int duration) {
		accelerations.add(new Acceleration(acceleration, duration));
	}
	
	@Override
	public void addGravity(Vector3 center, double power, int duration) {
		gravities.add(new Gravity(center, power, duration));
	}
	
	@Override
	public void tick(Entity entity) {
		Vector3 acc = getAcceleration(entity);
		if (!entity.world.isRemote) {
			entity.addVelocity(acc.x, acc.y, acc.z);
			if (Math.abs(acc.y) > 0.0001) {
				if (entity.getMotion().getY() >= 0) entity.fallDistance = 0;
				else if (acc.y > 0) {
					double invTermVel = 25 / 98.0;
					double y = entity.getMotion().getY() * invTermVel + 1;
					if (y > 0)
						entity.fallDistance = (float) Math.min(entity.fallDistance, Math.max(0, (-(49 / invTermVel)
								+ (((49 * y) - (Math.log(y) / Math.log(4 * invTermVel))) / invTermVel))));
				}
			}
			if (acc.y > 0 && entity instanceof ServerPlayerEntity) {
				ObfuscationReflectionHelper.setPrivateValue(ServerPlayNetHandler.class,
						((ServerPlayerEntity) entity).connection, false, "field_184344_B"); // floating
			}
		} else if (entity instanceof PlayerEntity) entity.addVelocity(acc.x, acc.y, acc.z);
		for (int i = accelerations.size() - 1; i >= 0; i--) {
			if (--accelerations.get(i).duration <= 0) accelerations.remove(i);
		}for (int i = gravities.size() - 1; i >= 0; i--) {
			if (--gravities.get(i).duration <= 0) gravities.remove(i);
		}
	}
	
	@SubscribeEvent
	public static void attach(AttachCapabilitiesEvent<Entity> event) {
		event.addCapability(new ResourceLocation(Phi.modId, "acceleration"),
				new ModCapabilities.Provider<>(ModCapabilities.acceleration, new AccelerationCapability()));
	}
	
	@SubscribeEvent
	public static void onTick(TickEvent.WorldTickEvent event) {
		if (event.phase != Phase.START || !(event.world instanceof ServerWorld)) return;
		((ServerWorld) event.world).getEntities().forEach(entity -> {
			entity.getCapability(ModCapabilities.acceleration).ifPresent(cap -> cap.tick(entity));
		});
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("resource")
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != Phase.START || Minecraft.getInstance().player == null) return;
		Minecraft.getInstance().player.getCapability(ModCapabilities.acceleration).ifPresent(cap -> {
			cap.tick(Minecraft.getInstance().player);
		});
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		ListNBT acc = new ListNBT();
		for (Acceleration a : accelerations) {
			CompoundNBT elem = new CompoundNBT();
			elem.putDouble("x", a.value.x);
			elem.putDouble("y", a.value.y);
			elem.putDouble("z", a.value.z);
			elem.putInt(tagDuration, a.duration);
			acc.add(elem);
		}
		nbt.put(tagAccelerations, acc);
		ListNBT grav = new ListNBT();
		for (Gravity g : gravities) {
			CompoundNBT elem = new CompoundNBT();
			elem.putDouble("x", g.center.x);
			elem.putDouble("y", g.center.y);
			elem.putDouble("z", g.center.z);
			elem.putDouble(tagPower, g.power);
			elem.putInt(tagDuration, g.duration);
			grav.add(elem);
		}
		nbt.put(tagGravities, grav);
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		ListNBT acc = nbt.getList(tagAccelerations, Constants.NBT.TAG_COMPOUND);
		accelerations = new ArrayList<>();
		for (int i = 0; i < acc.size(); i++) {
			CompoundNBT elem = acc.getCompound(i);
			accelerations
					.add(new Acceleration(new Vector3(elem.getDouble("x"), elem.getDouble("y"), elem.getDouble("z")),
							elem.getInt(tagDuration)));
		}
		ListNBT grav = nbt.getList(tagGravities, Constants.NBT.TAG_COMPOUND);
		gravities = new ArrayList<>();
		for (int i = 0; i < grav.size(); i++) {
			CompoundNBT elem = grav.getCompound(i);
			gravities
					.add(new Gravity(new Vector3(elem.getDouble("x"), elem.getDouble("y"), elem.getDouble("z")),
							elem.getDouble(tagPower), elem.getInt(tagDuration)));
		}
	}
	
	static class Acceleration {
		
		Vector3 value;
		int duration;
		
		Acceleration(Vector3 value, int duration) {
			this.value = value;
			this.duration = duration;
		}
		
	}
	
	static class Gravity {
		
		Vector3 center;
		double power;
		int duration;
		
		Gravity(Vector3 center, double power, int duration) {
			this.center = center;
			this.power = power;
			this.duration = duration;
		}
		
	}
	
}
