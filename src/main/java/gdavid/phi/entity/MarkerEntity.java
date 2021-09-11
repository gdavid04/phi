package gdavid.phi.entity;

import gdavid.phi.Phi;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import vazkii.psi.api.spell.ISpellImmune;

public class MarkerEntity extends Entity implements ISpellImmune {
	
	public static final String id = "marker";
	
	@ObjectHolder(Phi.modId + ":" + id)
	public static EntityType<MarkerEntity> type;
	
	static final String tagOwner = "owner";
	static final String tagTime = "time";
	
	public static final DataParameter<Optional<UUID>> owner = EntityDataManager.createKey(MarkerEntity.class,
			DataSerializers.OPTIONAL_UNIQUE_ID);
	
	int time;
	
	public MarkerEntity(EntityType<MarkerEntity> type, World world) {
		super(type, world);
	}
	
	public MarkerEntity(World world, Entity ownerEntity, int time) {
		super(type, world);
		if (owner != null) {
			dataManager.set(owner, Optional.of(ownerEntity.getUniqueID()));
		}
		this.time = time;
	}
	
	public UUID getOwner() {
		return dataManager.get(owner).get();
	}
	
	@Override
	protected void registerData() {
		dataManager.register(owner, Optional.of(new UUID(0, 0)));
	}
	
	@Override
	public void writeAdditional(CompoundNBT nbt) {
		nbt.putUniqueId(tagOwner, getOwner());
		nbt.putInt(tagTime, time);
	}
	
	@Override
	public void readAdditional(CompoundNBT nbt) {
		dataManager.set(owner, Optional.of(nbt.getUniqueId(tagOwner)));
		time = nbt.getInt(tagTime);
	}
	
	@Override
	public void tick() {
		super.tick();
		if (!world.isRemote && time-- < 0) remove();
	}
	
	@Override
	public boolean doesEntityNotTriggerPressurePlate() {
		return true;
	}
	
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	public boolean isImmune() {
		return true;
	}
	
}
