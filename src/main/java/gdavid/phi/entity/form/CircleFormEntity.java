package gdavid.phi.entity.form;

import gdavid.phi.Phi;
import gdavid.phi.api.util.ContextHelper;
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
import vazkii.psi.api.spell.SpellContext;

public class CircleFormEntity extends Entity implements ISpellImmune {
	
	public static final String id = "form_circle";
	
	@ObjectHolder(Phi.modId + ":" + id)
	public static EntityType<CircleFormEntity> type;
	
	static final String tagColorizer = "colorizer";
	static final String tagTime = "time";
	
	public static final DataParameter<ItemStack> colorizer = EntityDataManager.createKey(CircleFormEntity.class,
			DataSerializers.ITEMSTACK);
	public static final DataParameter<Integer> time = EntityDataManager.createKey(CircleFormEntity.class,
			DataSerializers.VARINT);
	
	SpellContext context;
	
	public CircleFormEntity(EntityType<CircleFormEntity> type, World world) {
		super(type, world);
	}
	
	public CircleFormEntity(World world, SpellContext context) {
		super(type, world);
		this.context = context;
	}
	
	public void setColorizer(ItemStack stack) {
		dataManager.set(colorizer, stack);
	}
	
	@Override
	protected void registerData() {
		dataManager.register(colorizer, ItemStack.EMPTY);
		dataManager.register(time, 0);
	}
	
	@Override
	public void writeAdditional(CompoundNBT nbt) {
		ItemStack colorizerItem = dataManager.get(colorizer);
		if (!colorizerItem.isEmpty()) {
			nbt.put(tagColorizer, colorizerItem.write(new CompoundNBT()));
		}
		nbt.putFloat(tagTime, dataManager.get(time));
	}
	
	@Override
	public void readAdditional(CompoundNBT nbt) {
		dataManager.set(colorizer, ItemStack.read(nbt.getCompound(tagColorizer)));
		dataManager.set(time, nbt.getInt(tagTime));
	}
	
	@Override
	public void tick() {
		super.tick();
		if (ticksExisted > 110) remove();
		dataManager.set(time, ticksExisted);
		if (context != null && !world.isRemote && ticksExisted > 5 && ticksExisted < 110 && ticksExisted % 5 == 0) {
			SpellContext ctx = ContextHelper.fork(context).setFocalPoint(this);
			ctx.loopcastIndex = (ticksExisted - 10) / 5;
			ctx.cspell.safeExecute(ctx);
		}
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
