package gdavid.phi.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.common.Psi;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.common.lib.ModTags;

import java.util.List;

public class PsimetalCrusherTile extends BlockEntity {
	
	public static BlockEntityType<PsimetalCrusherTile> type;
	
	public static final String tagProgress = "progress";
	
	private static final int duration = 60;
	private static final int craftTime = 5;
	
	public int progress = 0;
	
	public PsimetalCrusherTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public double getPistonOffset(float partialTicks) {
		float time = duration - progress + partialTicks;
		// start -> 0, lerp: craftTime -> 1, ease-in: end -> 0
		if (time <= 0) return 0;
		if (time < craftTime) return time / craftTime;
		return Math.pow(1 - (time - craftTime) / (duration - craftTime), 2);
	}
	
	public void tick() {
		if (progress > 0) {
			progress--;
			if (progress == duration - craftTime) {
				ItemEntity item = getItemUnder();
				if (item != null) {
					ItemStack stack = item.getItem();
					ItemStack from = stack.split(1);
					level.addFreshEntity(new ItemEntity(level, item.getX(), item.getY() + item.getBbHeight() / 2, item.getZ(), getResult(from)));
					if (stack.isEmpty()) item.discard();
					else item.setItem(stack);
					int color = ICADColorizer.DEFAULT_SPELL_COLOR;
					float r = ((color >> 16) & 0xFF) / 255f;
					float g = ((color >> 8) & 0xFF) / 255f;
					float b = (color & 0xFF) / 255f;
					for (int i = 0; i < 10; i++) {
						Psi.proxy.sparkleFX(
							item.getX(), item.getY(), item.getZ(), r, g, b,
								(float) level.random.nextGaussian() * 0.05f, (float) level.random.nextGaussian() * 0.05f, (float) level.random.nextGaussian() * 0.05f,
							3.5f, 15
						);
					}
				}
			}
		} else if (getItemUnder() != null) {
			progress = duration;
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 18);
		}
	}
	
	private static ItemStack getResult(ItemStack stack) {
		if (stack.is(ModTags.INGOT_PSIMETAL)) return new ItemStack(ModItems.psidust, 8);
		return ItemStack.EMPTY;
	}
	
	private ItemEntity getItemUnder() {
		AABB aabb = AABB.ofSize(Vec3.upFromBottomCenterOf(worldPosition, -1), 0.8, 1, 0.8);
		List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, aabb);
		return items.stream().filter(item -> item.getItem().is(ModTags.INGOT_PSIMETAL)).findAny().orElse(null);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt(tagProgress, progress);
		return nbt;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		progress = packet.getTag().getInt(tagProgress);
	}
	
}
