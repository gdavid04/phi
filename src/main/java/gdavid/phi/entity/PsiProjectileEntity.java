package gdavid.phi.entity;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.MPUTile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

public class PsiProjectileEntity extends ThrowableEntity {
	
	public static final String id = "psi_projectile";
	
	@ObjectHolder(Phi.modId + ":" + id)
	public static EntityType<PsiProjectileEntity> type;
	
	static final String tagColorizer = "colorizer";
	static final String tagDirectionX = "direction_x";
	static final String tagDirectionY = "direction_y";
	static final String tagDirectionZ = "direction_z";
	static final String tagOrigin = "origin";
	static final String tagTime = "time";
	static final String tagPsi = "psi";
	
	public static final DataParameter<ItemStack> colorizer = EntityDataManager.createKey(PsiProjectileEntity.class,
			DataSerializers.ITEMSTACK);
	public static final DataParameter<Float> directionX = EntityDataManager.createKey(PsiProjectileEntity.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> directionY = EntityDataManager.createKey(PsiProjectileEntity.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> directionZ = EntityDataManager.createKey(PsiProjectileEntity.class,
			DataSerializers.FLOAT);
	public static final DataParameter<BlockPos> origin = EntityDataManager.createKey(PsiProjectileEntity.class,
			DataSerializers.BLOCK_POS);
	public static final DataParameter<Integer> time = EntityDataManager.createKey(PsiProjectileEntity.class,
			DataSerializers.VARINT);
	public static final DataParameter<Integer> psi = EntityDataManager.createKey(PsiProjectileEntity.class,
			DataSerializers.VARINT);
	
	public PsiProjectileEntity(EntityType<PsiProjectileEntity> type, World world) {
		super(type, world);
	}
	
	public PsiProjectileEntity(World world, Vector3d direction, int psi) {
		super(type, world);
		dataManager.set(directionX, (float) direction.getX());
		dataManager.set(directionY, (float) direction.getY());
		dataManager.set(directionZ, (float) direction.getZ());
		dataManager.set(PsiProjectileEntity.psi, psi);
	}
	
	public void setColorizer(ItemStack stack) {
		dataManager.set(colorizer, stack);
	}
	
	public void setOrigin() {
		dataManager.set(origin, getPosition());
	}
	
	@Override
	protected void registerData() {
		dataManager.register(colorizer, ItemStack.EMPTY);
		dataManager.register(directionX, 0f);
		dataManager.register(directionY, 0f);
		dataManager.register(directionZ, 0f);
		dataManager.register(origin, BlockPos.ZERO);
		dataManager.register(time, 0);
		dataManager.register(psi, 0);
	}
	
	@Override
	public void writeAdditional(CompoundNBT nbt) {
		super.writeAdditional(nbt);
		ItemStack colorizerItem = dataManager.get(colorizer);
		if (!colorizerItem.isEmpty()) {
			nbt.put(tagColorizer, colorizerItem.write(new CompoundNBT()));
		}
		nbt.putFloat(tagDirectionX, dataManager.get(directionX));
		nbt.putFloat(tagDirectionY, dataManager.get(directionY));
		nbt.putFloat(tagDirectionZ, dataManager.get(directionZ));
		BlockPos originPos = dataManager.get(origin);
		nbt.putInt(tagOrigin + "_x", originPos.getX());
		nbt.putInt(tagOrigin + "_y", originPos.getY());
		nbt.putInt(tagOrigin + "_z", originPos.getZ());
		nbt.putFloat(tagTime, dataManager.get(time));
		nbt.putFloat(tagPsi, dataManager.get(psi));
	}
	
	@Override
	public void readAdditional(CompoundNBT nbt) {
		super.readAdditional(nbt);
		dataManager.set(colorizer, ItemStack.read(nbt.getCompound(tagColorizer)));
		dataManager.set(directionX, nbt.getFloat(tagDirectionX));
		dataManager.set(directionY, nbt.getFloat(tagDirectionY));
		dataManager.set(directionZ, nbt.getFloat(tagDirectionZ));
		dataManager.set(origin, new BlockPos(nbt.getInt(tagOrigin + "_x"), nbt.getInt(tagOrigin + "_y"), nbt.getInt(tagOrigin + "_z")));
		dataManager.set(time, nbt.getInt(tagTime));
		dataManager.set(psi, nbt.getInt(tagPsi));
	}
	
	@Override
	public void tick() {
		setMotion(dataManager.get(directionX) * 5 / 40,
				dataManager.get(directionY) * 5 / 40,
				dataManager.get(directionZ) * 5 / 40);
		super.tick();
		if (ticksExisted > 600) remove();
		dataManager.set(time, ticksExisted);
	}
	
	@Override
	protected void onImpact(RayTraceResult result) {
		if (world.isRemote) return;
		if (result instanceof BlockRayTraceResult) {
			BlockPos hit = ((BlockRayTraceResult) result).getPos();
			if (hit.equals(dataManager.get(origin))) return;
			TileEntity tile = world.getTileEntity(hit);
			if (tile instanceof MPUTile) {
				((MPUTile) tile).addPsi(dataManager.get(psi));
			}
		}
		remove();
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
