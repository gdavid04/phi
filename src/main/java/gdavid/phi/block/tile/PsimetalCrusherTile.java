package gdavid.phi.block.tile;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.common.lib.ModTags;

import java.util.List;

public class PsimetalCrusherTile extends TileEntity implements ITickableTileEntity {
	
	public static TileEntityType<PsimetalCrusherTile> type;
	
	public static final String tagProgress = "progress";
	
	private static final int duration = 60;
	private static final int craftTime = 5;
	
	public int progress = 0;
	
	public PsimetalCrusherTile() {
		super(type);
	}
	
	public double getPistonOffset(float partialTicks) {
		float time = duration - progress + partialTicks;
		// start -> 0, lerp: craftTime -> 1, ease-in: end -> 0
		if (time <= 0) return 0;
		if (time < craftTime) return time / craftTime;
		return Math.pow(1 - (time - craftTime) / (duration - craftTime), 2);
	}
	
	@Override
	public void tick() {
		if (progress > 0) {
			progress--;
			if (progress == duration - craftTime) {
				ItemEntity item = getItemUnder();
				if (item != null) {
					ItemStack stack = item.getItem();
					ItemStack from = stack.split(1);
					if (stack.isEmpty()) item.remove();
					else item.setItem(stack);
					world.addEntity(new ItemEntity(world, item.getPosX(), item.getPosY(), item.getPosZ(), getResult(from)));
				}
			}
		} else if (getItemUnder() != null) {
			progress = duration;
			world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
		}
	}
	
	private static ItemStack getResult(ItemStack stack) {
		if (stack.getItem().isIn(ModTags.INGOT_PSIMETAL)) return new ItemStack(ModItems.psidust, 8);
		return ItemStack.EMPTY;
	}
	
	private ItemEntity getItemUnder() {
		AxisAlignedBB aabb = AxisAlignedBB.withSizeAtOrigin(0.8, 1, 0.8).offset(Vector3d.copyCenteredWithVerticalOffset(pos, -1));
		List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, aabb);
		return items.stream().filter(item -> item.getItem().getItem().isIn(ModTags.INGOT_PSIMETAL)).findAny().orElse(null);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getPos(), 0, write(new CompoundNBT()));
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt(tagProgress, progress);
		return nbt;
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		progress = packet.getNbtCompound().getInt(tagProgress);
	}
	
}
