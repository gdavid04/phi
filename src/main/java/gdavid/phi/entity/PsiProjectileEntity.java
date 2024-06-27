package gdavid.phi.entity;

import gdavid.phi.Phi;
import gdavid.phi.util.IPsiAcceptor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.common.block.base.ModBlocks;

public class PsiProjectileEntity extends ThrowableProjectile {
	
	public static final String id = "psi_projectile";
	
	@ObjectHolder(registryName = "entity_type", value = Phi.modId + ":" + id)
	public static EntityType<PsiProjectileEntity> type;
	
	static final String tagColorizer = "colorizer";
	static final String tagDirectionX = "direction_x";
	static final String tagDirectionY = "direction_y";
	static final String tagDirectionZ = "direction_z";
	static final String tagOrigin = "origin";
	static final String tagTime = "time";
	static final String tagPsi = "psi";
	
	public static final EntityDataAccessor<ItemStack> colorizer = SynchedEntityData.defineId(PsiProjectileEntity.class,
			EntityDataSerializers.ITEM_STACK);
	public static final EntityDataAccessor<Float> directionX = SynchedEntityData.defineId(PsiProjectileEntity.class,
			EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> directionY = SynchedEntityData.defineId(PsiProjectileEntity.class,
			EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> directionZ = SynchedEntityData.defineId(PsiProjectileEntity.class,
			EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<BlockPos> origin = SynchedEntityData.defineId(PsiProjectileEntity.class,
			EntityDataSerializers.BLOCK_POS);
	public static final EntityDataAccessor<Integer> time = SynchedEntityData.defineId(PsiProjectileEntity.class,
			EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> psi = SynchedEntityData.defineId(PsiProjectileEntity.class,
			EntityDataSerializers.INT);
	
	public PsiProjectileEntity(EntityType<PsiProjectileEntity> type, Level world) {
		super(type, world);
	}
	
	public PsiProjectileEntity(Level world, Vec3 direction, int psi) {
		super(type, world);
		entityData.set(directionX, (float) direction.x());
		entityData.set(directionY, (float) direction.y());
		entityData.set(directionZ, (float) direction.z());
		entityData.set(PsiProjectileEntity.psi, psi);
	}
	
	public void setColorizer(ItemStack stack) {
		entityData.set(colorizer, stack);
	}
	
	public void setOrigin() {
		entityData.set(origin, blockPosition());
	}
	
	@Override
	protected void defineSynchedData() {
		entityData.define(colorizer, ItemStack.EMPTY);
		entityData.define(directionX, 0f);
		entityData.define(directionY, 0f);
		entityData.define(directionZ, 0f);
		entityData.define(origin, BlockPos.ZERO);
		entityData.define(time, 0);
		entityData.define(psi, 0);
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		ItemStack colorizerItem = entityData.get(colorizer);
		if (!colorizerItem.isEmpty()) {
			nbt.put(tagColorizer, colorizerItem.save(new CompoundTag()));
		}
		nbt.putFloat(tagDirectionX, entityData.get(directionX));
		nbt.putFloat(tagDirectionY, entityData.get(directionY));
		nbt.putFloat(tagDirectionZ, entityData.get(directionZ));
		BlockPos originPos = entityData.get(origin);
		nbt.putInt(tagOrigin + "_x", originPos.getX());
		nbt.putInt(tagOrigin + "_y", originPos.getY());
		nbt.putInt(tagOrigin + "_z", originPos.getZ());
		nbt.putFloat(tagTime, entityData.get(time));
		nbt.putFloat(tagPsi, entityData.get(psi));
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		entityData.set(colorizer, ItemStack.of(nbt.getCompound(tagColorizer)));
		entityData.set(directionX, nbt.getFloat(tagDirectionX));
		entityData.set(directionY, nbt.getFloat(tagDirectionY));
		entityData.set(directionZ, nbt.getFloat(tagDirectionZ));
		entityData.set(origin,
				new BlockPos(nbt.getInt(tagOrigin + "_x"), nbt.getInt(tagOrigin + "_y"), nbt.getInt(tagOrigin + "_z")));
		entityData.set(time, nbt.getInt(tagTime));
		entityData.set(psi, nbt.getInt(tagPsi));
	}
	
	@Override
	public void tick() {
		setDeltaMovement(entityData.get(directionX) * 12 / 40, entityData.get(directionY) * 12 / 40,
				entityData.get(directionZ) * 12 / 40);
		super.tick();
		if (tickCount > 240) discard();
		entityData.set(time, tickCount);
	}
	
	@Override
	protected void onHit(HitResult result) {
		if (level.isClientSide) return;
		if (result instanceof BlockHitResult) {
			BlockPos hit = ((BlockHitResult) result).getBlockPos();
			if (level.getBlockState(hit).is(ModBlocks.conjured)) return;
			if (hit.equals(entityData.get(origin))) return;
			BlockEntity tile = level.getBlockEntity(hit);
			if (tile instanceof IPsiAcceptor) {
				((IPsiAcceptor) tile).addPsi(entityData.get(psi));
			}
		}
		discard();
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
	
}
