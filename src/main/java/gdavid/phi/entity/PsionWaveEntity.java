package gdavid.phi.entity;

import gdavid.phi.Phi;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.IPlayerData;

public class PsionWaveEntity extends ThrowableEntity {
	
	public static final String id = "psion_wave";
	
	@ObjectHolder(Phi.modId + ":" + id)
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
	
	public static final DataParameter<ItemStack> colorizer = EntityDataManager.createKey(PsionWaveEntity.class,
			DataSerializers.ITEMSTACK);
	public static final DataParameter<Optional<UUID>> shooter = EntityDataManager.createKey(PsionWaveEntity.class,
			DataSerializers.OPTIONAL_UNIQUE_ID);
	public static final DataParameter<Float> directionX = EntityDataManager.createKey(PsionWaveEntity.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> directionY = EntityDataManager.createKey(PsionWaveEntity.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> directionZ = EntityDataManager.createKey(PsionWaveEntity.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> speed = EntityDataManager.createKey(PsionWaveEntity.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> frequency = EntityDataManager.createKey(PsionWaveEntity.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> distance = EntityDataManager.createKey(PsionWaveEntity.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> traveled = EntityDataManager.createKey(PsionWaveEntity.class,
			DataSerializers.FLOAT);
	
	public PsionWaveEntity(EntityType<PsionWaveEntity> type, World world) {
		super(type, world);
	}
	
	public PsionWaveEntity(World world, Vector3f direction, float speed, float frequency, float distance) {
		super(type, world);
		dataManager.set(directionX, direction.getX());
		dataManager.set(directionY, direction.getY());
		dataManager.set(directionZ, direction.getZ());
		dataManager.set(PsionWaveEntity.speed, speed);
		dataManager.set(PsionWaveEntity.frequency, frequency);
		dataManager.set(PsionWaveEntity.distance, distance);
	}
	
	@Override
	public void setShooter(Entity entity) {
		super.setShooter(entity);
		dataManager.set(shooter, Optional.of(entity.getUniqueID()));
	}
	
	public void setColorizer(ItemStack stack) {
		dataManager.set(colorizer, stack);
	}
	
	@Override
	protected void registerData() {
		dataManager.register(colorizer, ItemStack.EMPTY);
		dataManager.register(shooter, Optional.of(new UUID(0, 0)));
		dataManager.register(directionX, 0f);
		dataManager.register(directionY, 0f);
		dataManager.register(directionZ, 0f);
		dataManager.register(speed, 1f);
		dataManager.register(frequency, 0f);
		dataManager.register(distance, 0f);
		dataManager.register(traveled, 0f);
	}
	
	@Override
	public void writeAdditional(CompoundNBT nbt) {
		super.writeAdditional(nbt);
		ItemStack colorizerItem = dataManager.get(colorizer);
		if (!colorizerItem.isEmpty()) {
			nbt.put(tagColorizer, colorizerItem.write(new CompoundNBT()));
		}
		nbt.putUniqueId(tagShooter, dataManager.get(shooter).get());
		nbt.putFloat(tagDirectionX, dataManager.get(directionX));
		nbt.putFloat(tagDirectionY, dataManager.get(directionY));
		nbt.putFloat(tagDirectionZ, dataManager.get(directionZ));
		nbt.putFloat(tagSpeed, dataManager.get(speed));
		nbt.putFloat(tagFrequency, dataManager.get(frequency));
		nbt.putFloat(tagDistance, dataManager.get(distance));
		nbt.putFloat(tagTraveled, dataManager.get(traveled));
	}
	
	@Override
	public void readAdditional(CompoundNBT nbt) {
		super.readAdditional(nbt);
		dataManager.set(colorizer, ItemStack.read(nbt.getCompound(tagColorizer)));
		dataManager.set(shooter, Optional.of(nbt.getUniqueId(tagShooter)));
		dataManager.set(directionX, nbt.getFloat(tagDirectionX));
		dataManager.set(directionY, nbt.getFloat(tagDirectionY));
		dataManager.set(directionZ, nbt.getFloat(tagDirectionZ));
		dataManager.set(speed, nbt.getFloat(tagSpeed));
		dataManager.set(frequency, nbt.getFloat(tagFrequency));
		dataManager.set(distance, nbt.getFloat(tagDistance));
		dataManager.set(traveled, nbt.getFloat(tagTraveled));
	}
	
	@Override
	public void tick() {
		setMotion(dataManager.get(directionX) * dataManager.get(speed) / 40,
				dataManager.get(directionY) * dataManager.get(speed) / 40,
				dataManager.get(directionZ) * dataManager.get(speed) / 40);
		super.tick();
		dataManager.set(traveled, dataManager.get(traveled) + dataManager.get(speed) / 40);
		if (dataManager.get(traveled) > dataManager.get(distance)) {
			remove();
		}
	}
	
	@Override
	protected void onImpact(RayTraceResult result) {
		if (result instanceof EntityRayTraceResult) {
			Entity hit = ((EntityRayTraceResult) result).getEntity();
			if (hit.getUniqueID().equals(dataManager.get(shooter).get()) && dataManager.get(traveled) < 0.8) {
				return;
			}
			if (hit instanceof PlayerEntity && !world.isRemote) {
				PlayerEntity player = (PlayerEntity) hit;
				IPlayerData data = PsiAPI.internalHandler.getDataForPlayer(player);
				float traveledPercent = dataManager.get(traveled) / dataManager.get(distance);
				float focus = 2 * traveledPercent * (traveledPercent - 1) + 1;
				data.deductPsi((int) Math.ceil(dataManager.get(frequency) * 10 * focus),
						(int) Math.ceil(dataManager.get(frequency) * 2 * focus), true, true);
				EffectInstance effect = player.getActivePotionEffect(Effects.SLOWNESS);
				int newTime;
				if (effect == null) {
					newTime = (int) Math.ceil(dataManager.get(frequency) * focus);
				} else {
					newTime = (int) Math
							.ceil(Math.pow(dataManager.get(frequency) * focus + effect.getDuration(), 1 + 0.2 * focus));
				}
				player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, newTime % 600, newTime / 60, false, false));
			}
			remove();
		}
	}
	
	@Override
	protected float getGravityVelocity() {
		return 0;
	}
	
	@Override
	public boolean doesEntityNotTriggerPressurePlate() {
		return true;
	}
	
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
}
