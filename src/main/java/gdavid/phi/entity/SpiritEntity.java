package gdavid.phi.entity;

import gdavid.phi.Phi;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.spell.ISpellImmune;
import vazkii.psi.common.Psi;

public class SpiritEntity extends Entity implements ISpellImmune {
	
	public static final String id = "spirit";
	
	@ObjectHolder(registryName = "entity_type", value = Phi.modId + ":" + id)
	public static EntityType<SpiritEntity> type;
	
	static final String tagOwner = "owner";
	static final String tagTime = "time";
	
	public static final EntityDataAccessor<Optional<UUID>> owner = SynchedEntityData.defineId(SpiritEntity.class,
			EntityDataSerializers.OPTIONAL_UUID);
	
	int time;
	
	public SpiritEntity(EntityType<SpiritEntity> type, Level world) {
		super(type, world);
	}
	
	public SpiritEntity(Level world, Entity ownerEntity, int time) {
		super(type, world);
		if (owner != null) {
			entityData.set(owner, Optional.of(ownerEntity.getUUID()));
		}
		this.time = time;
	}
	
	public UUID getOwner() {
		return entityData.get(owner).get();
	}
	
	@Override
	protected void defineSynchedData() {
		entityData.define(owner, Optional.of(new UUID(0, 0)));
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		nbt.putUUID(tagOwner, getOwner());
		nbt.putInt(tagTime, time);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		entityData.set(owner, Optional.of(nbt.getUUID(tagOwner)));
		time = nbt.getInt(tagTime);
	}
	
	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide && random.nextFloat() < 0.1f) {
			int color = ICADColorizer.DEFAULT_SPELL_COLOR;
			float r = ((color >> 16) & 0xFF) / 255f;
			float g = ((color >> 8) & 0xFF) / 255f;
			float b = (color & 0xFF) / 255f;
			Psi.proxy.wispFX(
				level,
				getX(), getY() + getBbHeight() / 2, getZ(),
				r, g, b,
				0.1f + random.nextFloat() * 0.05f,
				(float) random.nextGaussian() * 0.0025f, (float) (random.nextGaussian() * 0.0025f - 0.005f), (float) random.nextGaussian() * 0.0025f,
				2
			);
		}
		if (!level.isClientSide && time-- < 0) discard();
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
