package gdavid.phi.capability;

import gdavid.phi.Phi;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import vazkii.psi.api.internal.Vector3;

@EventBusSubscriber
public class AccelerationCapability implements IAccelerationCapability, INBTSerializable<CompoundTag> {
	
	static final String tagAccelerations = "accelerations";
	static final String tagGravities = "gravities";
	static final String tagPower = "power";
	static final String tagDuration = "duration";
	
	List<Acceleration> accelerations = new ArrayList<>();
	List<AccelerationTowardsPoint> accelerationsTowardsPoint = new ArrayList<>();
	
	@Override
	public Vector3 getAcceleration(Entity entity) {
		Vector3 res = new Vector3();
		for (Acceleration a : accelerations) {
			res.add(a.value);
		}
		for (AccelerationTowardsPoint g : accelerationsTowardsPoint) {
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
	public void addAccelerationTowardsPoint(Vector3 center, double power, int duration) {
		accelerationsTowardsPoint.add(new AccelerationTowardsPoint(center, power, duration));
	}
	
	@Override
	public void tick(Entity entity) {
		Vector3 acc = getAcceleration(entity);
		if (!entity.level.isClientSide) {
			entity.push(acc.x, acc.y, acc.z);
			if (Math.abs(acc.y) > 0.0001) {
				if (entity.getDeltaMovement().y() >= 0) entity.fallDistance = 0;
				else if (acc.y > 0) {
					double invTermVel = 25 / 98.0;
					double y = entity.getDeltaMovement().y() * invTermVel + 1;
					if (y > 0)
						entity.fallDistance = (float) Math.min(entity.fallDistance, Math.max(0, (-(49 / invTermVel)
								+ (((49 * y) - (Math.log(y) / Math.log(4 * invTermVel))) / invTermVel))));
				}
			}
			if (acc.y > 0 && entity instanceof ServerPlayer) {
				ObfuscationReflectionHelper.setPrivateValue(ServerGamePacketListenerImpl.class,
						((ServerPlayer) entity).connection, false, "clientIsFloating");
			}
		} else if (entity instanceof Player) entity.push(acc.x, acc.y, acc.z);
		for (int i = accelerations.size() - 1; i >= 0; i--) {
			if (--accelerations.get(i).duration <= 0) accelerations.remove(i);
		}
		for (int i = accelerationsTowardsPoint.size() - 1; i >= 0; i--) {
			if (--accelerationsTowardsPoint.get(i).duration <= 0) accelerationsTowardsPoint.remove(i);
		}
	}
	
	@SubscribeEvent
	public static void attach(AttachCapabilitiesEvent<Entity> event) {
		event.addCapability(new ResourceLocation(Phi.modId, "acceleration"),
				new ModCapabilities.Provider<>(ModCapabilities.acceleration, new AccelerationCapability()));
	}
	
	@SubscribeEvent
	public static void onTick(LevelTickEvent event) {
		if (event.phase != Phase.START || !(event.level instanceof ServerLevel l)) return;
		l.getEntities().getAll().forEach(entity -> {
			entity.getCapability(ModCapabilities.acceleration).ifPresent(cap -> cap.tick(entity));
		});
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != Phase.START || Minecraft.getInstance().player == null
				|| Minecraft.getInstance().isPaused())
			return;
		Minecraft.getInstance().player.getCapability(ModCapabilities.acceleration).ifPresent(cap -> {
			cap.tick(Minecraft.getInstance().player);
		});
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		ListTag acc = new ListTag();
		for (Acceleration a : accelerations) {
			CompoundTag elem = new CompoundTag();
			elem.putDouble("x", a.value.x);
			elem.putDouble("y", a.value.y);
			elem.putDouble("z", a.value.z);
			elem.putInt(tagDuration, a.duration);
			acc.add(elem);
		}
		nbt.put(tagAccelerations, acc);
		ListTag grav = new ListTag();
		for (AccelerationTowardsPoint g : accelerationsTowardsPoint) {
			CompoundTag elem = new CompoundTag();
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
	public void deserializeNBT(CompoundTag nbt) {
		ListTag acc = nbt.getList(tagAccelerations, CompoundTag.TAG_COMPOUND);
		accelerations = new ArrayList<>();
		for (int i = 0; i < acc.size(); i++) {
			CompoundTag elem = acc.getCompound(i);
			accelerations
					.add(new Acceleration(new Vector3(elem.getDouble("x"), elem.getDouble("y"), elem.getDouble("z")),
							elem.getInt(tagDuration)));
		}
		ListTag grav = nbt.getList(tagGravities, CompoundTag.TAG_COMPOUND);
		accelerationsTowardsPoint = new ArrayList<>();
		for (int i = 0; i < grav.size(); i++) {
			CompoundTag elem = grav.getCompound(i);
			accelerationsTowardsPoint.add(new AccelerationTowardsPoint(
					new Vector3(elem.getDouble("x"), elem.getDouble("y"), elem.getDouble("z")),
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
	
	static class AccelerationTowardsPoint {
		
		Vector3 center;
		double power;
		int duration;
		
		AccelerationTowardsPoint(Vector3 center, double power, int duration) {
			this.center = center;
			this.power = power;
			this.duration = duration;
		}
		
	}
	
}
