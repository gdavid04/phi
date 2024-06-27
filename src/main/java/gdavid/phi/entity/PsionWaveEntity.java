package gdavid.phi.entity;

import gdavid.phi.Phi;
import gdavid.phi.util.IWaveImpacted;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.spell.ISpellImmune;

import java.util.Optional;
import java.util.UUID;

public class PsionWaveEntity extends ThrowableProjectile implements ISpellImmune {
	
	public static final String id = "psion_wave";
	
	@ObjectHolder(registryName = "entity_type", value = Phi.modId + ":" + id)
	public static EntityType<PsionWaveEntity> type;
	
	static final String tagColorizer = "colorizer";
	static final String tagShooter = "shooter";
	static final String tagDirectionX = "direction_x";
	static final String tagDirectionY = "direction_y";
	static final String tagDirectionZ = "direction_z";
	static final String tagSpeed = "speed";
	static final String tagFrequency = "frequency";
	static final String tagDistance = "distance";
	static final String tagTraveled = "traveled";
	
	public static final EntityDataAccessor<ItemStack> colorizer = SynchedEntityData.defineId(PsionWaveEntity.class,
			EntityDataSerializers.ITEM_STACK);
	public static final EntityDataAccessor<Optional<UUID>> shooter = SynchedEntityData.defineId(PsionWaveEntity.class,
			EntityDataSerializers.OPTIONAL_UUID);
	public static final EntityDataAccessor<Float> directionX = SynchedEntityData.defineId(PsionWaveEntity.class,
			EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> directionY = SynchedEntityData.defineId(PsionWaveEntity.class,
			EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> directionZ = SynchedEntityData.defineId(PsionWaveEntity.class,
			EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> speed = SynchedEntityData.defineId(PsionWaveEntity.class,
			EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> frequency = SynchedEntityData.defineId(PsionWaveEntity.class,
			EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> distance = SynchedEntityData.defineId(PsionWaveEntity.class,
			EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> traveled = SynchedEntityData.defineId(PsionWaveEntity.class,
			EntityDataSerializers.FLOAT);
	
	public PsionWaveEntity(EntityType<PsionWaveEntity> type, Level world) {
		super(type, world);
	}
	
	public PsionWaveEntity(Level world, Vector3f direction, float speed, float frequency, float distance) {
		super(type, world);
		entityData.set(directionX, direction.x());
		entityData.set(directionY, direction.y());
		entityData.set(directionZ, direction.z());
		entityData.set(PsionWaveEntity.speed, speed);
		entityData.set(PsionWaveEntity.frequency, frequency);
		entityData.set(PsionWaveEntity.distance, distance);
	}
	
	@Override
	public void setOwner(Entity entity) {
		super.setOwner(entity);
		entityData.set(shooter, Optional.of(entity.getUUID()));
	}
	
	public void setColorizer(ItemStack stack) {
		entityData.set(colorizer, stack);
	}
	
	@Override
	protected void defineSynchedData() {
		entityData.define(colorizer, ItemStack.EMPTY);
		entityData.define(shooter, Optional.of(new UUID(0, 0)));
		entityData.define(directionX, 0f);
		entityData.define(directionY, 0f);
		entityData.define(directionZ, 0f);
		entityData.define(speed, 1f);
		entityData.define(frequency, 0f);
		entityData.define(distance, 0f);
		entityData.define(traveled, 0f);
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		ItemStack colorizerItem = entityData.get(colorizer);
		if (!colorizerItem.isEmpty()) {
			nbt.put(tagColorizer, colorizerItem.save(new CompoundTag()));
		}
		nbt.putUUID(tagShooter, entityData.get(shooter).get());
		nbt.putFloat(tagDirectionX, entityData.get(directionX));
		nbt.putFloat(tagDirectionY, entityData.get(directionY));
		nbt.putFloat(tagDirectionZ, entityData.get(directionZ));
		nbt.putFloat(tagSpeed, entityData.get(speed));
		nbt.putFloat(tagFrequency, entityData.get(frequency));
		nbt.putFloat(tagDistance, entityData.get(distance));
		nbt.putFloat(tagTraveled, entityData.get(traveled));
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		entityData.set(colorizer, ItemStack.of(nbt.getCompound(tagColorizer)));
		entityData.set(shooter, Optional.of(nbt.getUUID(tagShooter)));
		entityData.set(directionX, nbt.getFloat(tagDirectionX));
		entityData.set(directionY, nbt.getFloat(tagDirectionY));
		entityData.set(directionZ, nbt.getFloat(tagDirectionZ));
		entityData.set(speed, nbt.getFloat(tagSpeed));
		entityData.set(frequency, nbt.getFloat(tagFrequency));
		entityData.set(distance, nbt.getFloat(tagDistance));
		entityData.set(traveled, nbt.getFloat(tagTraveled));
	}
	
	@Override
	public void tick() {
		setDeltaMovement(entityData.get(directionX) * entityData.get(speed) / 40,
				entityData.get(directionY) * entityData.get(speed) / 40,
				entityData.get(directionZ) * entityData.get(speed) / 40);
		super.tick();
		entityData.set(traveled, entityData.get(traveled) + entityData.get(speed) / 40);
		if (entityData.get(traveled) > entityData.get(distance)) {
			discard();
		}
	}
	
	@Override
	protected void onHit(HitResult result) {
		float traveledPercent = entityData.get(traveled) / entityData.get(distance);
		float focus = 2 * traveledPercent * (traveledPercent - 1) + 1;
		if (result instanceof EntityHitResult) {
			Entity hit = ((EntityHitResult) result).getEntity();
			if (hit.getUUID().equals(entityData.get(shooter).get()) && entityData.get(traveled) < 0.8) {
				return;
			}
			if (hit instanceof Player && !level.isClientSide) {
				Player player = (Player) hit;
				IPlayerData data = PsiAPI.internalHandler.getDataForPlayer(player);
				data.deductPsi((int) Math.ceil(entityData.get(frequency) * 10 * focus),
						(int) Math.ceil(entityData.get(frequency) * 2 * focus), true, true);
				MobEffectInstance effect = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
				int newTime;
				if (effect == null) {
					newTime = (int) Math.ceil(entityData.get(frequency) * focus);
				} else {
					newTime = (int) Math
							.ceil(Math.pow(entityData.get(frequency) * focus + effect.getDuration(), 1 + 0.2 * focus));
				}
				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, newTime % 600, newTime / 60, false, false));
			}
			discard();
		} else if (result instanceof BlockHitResult) {
			BlockEntity tile = level.getBlockEntity(((BlockHitResult) result).getBlockPos());
			if (tile instanceof IWaveImpacted) {
				((IWaveImpacted) tile).waveImpact(entityData.get(frequency), focus);
				discard();
			}
		}
	}
	
	@Override
	protected float getGravity() {
		return 0;
	}
	
	@Override
	public boolean isIgnoringBlockTriggers() {
		return true;
	}
	
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	public boolean isImmune() {
		return true;
	}
	
}
