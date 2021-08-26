package gdavid.phi.entity;

import gdavid.phi.Phi;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
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
	
	static final String tagColorizer = "colorizer";
	static final String tagOwner = "owner";
	static final String tagTime = "time";
	
	public static final DataParameter<ItemStack> colorizer = EntityDataManager.createKey(MarkerEntity.class,
			DataSerializers.ITEMSTACK);
	public static final DataParameter<Optional<UUID>> owner = EntityDataManager.createKey(MarkerEntity.class,
			DataSerializers.OPTIONAL_UNIQUE_ID);
	
	int time;
	
	public MarkerEntity(EntityType<MarkerEntity> type, World world) {
		super(type, world);
	}
	
	public MarkerEntity(World world, ItemStack colorizerStack, Entity ownerEntity, int time) {
		super(type, world);
		dataManager.set(colorizer, colorizerStack);
		if (owner != null) {
			dataManager.set(owner, Optional.of(ownerEntity.getUniqueID()));
		}
		this.time = time;
	}
	
	@Override
	protected void registerData() {
		dataManager.register(colorizer, ItemStack.EMPTY);
		dataManager.register(owner, Optional.of(new UUID(0, 0)));
	}
	
	@Override
	public void writeAdditional(CompoundNBT nbt) {
		ItemStack colorizerItem = dataManager.get(colorizer);
		if (!colorizerItem.isEmpty()) {
			nbt.put(tagColorizer, colorizerItem.write(new CompoundNBT()));
		}
		nbt.putUniqueId(tagOwner, dataManager.get(owner).get());
		nbt.putInt(tagTime, time);
	}
	
	@Override
	public void readAdditional(CompoundNBT nbt) {
		dataManager.set(colorizer, ItemStack.read(nbt.getCompound(tagColorizer)));
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
