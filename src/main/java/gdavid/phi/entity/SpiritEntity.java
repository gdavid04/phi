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
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.spell.ISpellImmune;
import vazkii.psi.common.Psi;

public class SpiritEntity extends Entity implements ISpellImmune {
	
	public static final String id = "spirit";
	
	@ObjectHolder(Phi.modId + ":" + id)
	public static EntityType<SpiritEntity> type;
	
	static final String tagOwner = "owner";
	static final String tagTime = "time";
	
	public static final DataParameter<Optional<UUID>> owner = EntityDataManager.createKey(SpiritEntity.class,
			DataSerializers.OPTIONAL_UNIQUE_ID);
	
	int time;
	
	public SpiritEntity(EntityType<SpiritEntity> type, World world) {
		super(type, world);
	}
	
	public SpiritEntity(World world, Entity ownerEntity, int time) {
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
		if (world.isRemote && rand.nextFloat() < 0.1f) {
			int color = ICADColorizer.DEFAULT_SPELL_COLOR;
			float r = ((color >> 16) & 0xFF) / 255f;
			float g = ((color >> 8) & 0xFF) / 255f;
			float b = (color & 0xFF) / 255f;
			Psi.proxy.wispFX(
				world,
				getPosX(), getPosY() + getHeight() / 2, getPosZ(),
				r, g, b,
				0.1f + rand.nextFloat() * 0.05f,
				(float) rand.nextGaussian() * 0.0025f, (float) (rand.nextGaussian() * 0.0025f - 0.005f), (float) rand.nextGaussian() * 0.0025f,
				2
			);
		}
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
