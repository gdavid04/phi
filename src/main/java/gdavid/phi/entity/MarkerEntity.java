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
import vazkii.psi.api.spell.ISpellImmune;

public class MarkerEntity extends Entity implements ISpellImmune {
	
	public static final String id = "marker";
	
	@ObjectHolder(registryName = "entity_type", value = Phi.modId + ":" + id)
	public static EntityType<MarkerEntity> type;
	
	static final String tagOwner = "owner";
	static final String tagTime = "time";
	
	public static final EntityDataAccessor<Optional<UUID>> owner = SynchedEntityData.defineId(MarkerEntity.class,
			EntityDataSerializers.OPTIONAL_UUID);
	
	int time;
	
	public MarkerEntity(EntityType<MarkerEntity> type, Level world) {
		super(type, world);
	}
	
	public MarkerEntity(Level world, Entity ownerEntity, int time) {
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
