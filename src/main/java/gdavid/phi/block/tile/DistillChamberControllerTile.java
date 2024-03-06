package gdavid.phi.block.tile;

import gdavid.phi.block.DistillChamberControllerBlock;
import gdavid.phi.block.DistillChamberWallBlock;
import gdavid.phi.entity.PsiProjectileEntity;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import vazkii.psi.common.lib.ModTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistillChamberControllerTile extends BlockEntity {
	
	public static BlockEntityType<DistillChamberControllerTile> type;
	
	public static final String tagFuel = "fuel";
	public static final String tagItem = "item";
	public static final String tagValue = "value";
	public static final String tagPsi = "psi";
	
	private static final int storagePerBlock = 4;
	
	private List<Pair<ItemStack, Integer>> fuel = new ArrayList<>();
	private int psi = 0;
	
	public DistillChamberControllerTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public void tick() {
		int size = getStructureVolume();
		producePsi(size);
		if (psi >= 100) {
			PsiProjectileEntity projectile = new PsiProjectileEntity(level, Vec3.atLowerCornerOf(getBlockState().getValue(DistillChamberControllerBlock.FACING).getNormal()), psi);
			projectile.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
			projectile.setOrigin();
			level.addFreshEntity(projectile);
			psi = 0;
			setChanged();
		}
		if (fuel.size() < size * storagePerBlock) {
			ItemEntity item = getFuelItem();
			System.out.println(item);
			if (item != null) {
				ItemStack stack = item.getItem();
				ItemStack fuelStack = stack.split(1);
				fuel.add(Pair.of(fuelStack, getValue(fuelStack)));
				if (stack.isEmpty()) item.discard();
				else item.setItem(stack);
				setChanged();
			}
		}
	}
	
	private void producePsi(int size) {
		int amount = 0;
		boolean changed = false;
		for (int i = 0; i < fuel.size() && amount < size; i++) {
			ItemStack stack = fuel.get(i).getKey();
			int value = fuel.get(i).getValue();
			amount++;
			if (value > 1) fuel.set(i, Pair.of(stack, value - 1));
			else {
				if (stack.isEmpty()) fuel.remove(i--);
				else {
					stack.shrink(1);
					fuel.set(i, Pair.of(stack, getValue(stack)));
				}
			}
			changed = true;
		}
		psi += amount;
		if (changed) setChanged();
	}
	
	private ItemEntity getFuelItem() {
		AABB aabb = AABB.ofSize(Vec3.atCenterOf(worldPosition.relative(getBlockState().getValue(DistillChamberControllerBlock.FACING))), 1, 1, 1);
		List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, aabb);
		return items.stream().filter(item -> item.getItem().is(ModTags.PSIDUST)).findAny().orElse(null);
	}
	
	private int getValue(ItemStack stack) {
		if (stack.is(ModTags.PSIDUST)) return 625;
		return 0;
	}
	
	private int getStructureVolume() {
		Direction dir = getBlockState().getValue(DistillChamberControllerBlock.FACING).getOpposite(); // interior direction
		Map<Direction, Integer> edges = new HashMap<>(6);
		BlockPos edgePos = null;
		for (Direction side : Direction.values()) { // determine the size of the face the controller is on
			if (side.getAxis() == dir.getAxis()) continue;
			int i = findEdge(worldPosition, side);
			int opos = side.getAxis().choose(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
			if (side.getAxisDirection() == Direction.AxisDirection.POSITIVE) opos += i;
			else opos -= i;
			edges.put(side, opos);
			if (edgePos == null) edgePos = worldPosition.relative(side, i * side.getAxisDirection().getStep());
		}
		int depth = findEdge(edgePos, dir); // determine depth at an edge
		edges.put(dir, dir.getAxis().choose(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) + depth * dir.getAxisDirection().getStep());
		edges.put(dir.getOpposite(), dir.getAxis().choose(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()));
		int x1 = edges.get(Direction.get(Direction.AxisDirection.NEGATIVE, Axis.X));
		int y1 = edges.get(Direction.get(Direction.AxisDirection.NEGATIVE, Axis.Y));
		int z1 = edges.get(Direction.get(Direction.AxisDirection.NEGATIVE, Axis.Z));
		int x2 = edges.get(Direction.get(Direction.AxisDirection.POSITIVE, Axis.X));
		int y2 = edges.get(Direction.get(Direction.AxisDirection.POSITIVE, Axis.Y));
		int z2 = edges.get(Direction.get(Direction.AxisDirection.POSITIVE, Axis.Z));
		if (!verifyStructure(x1, y1, z1, x2, y2, z2)) return 0;
		return (x2 - x1 - 1) * (y2 - y1 - 1) * (z2 - z1 - 1); // calculate internal volume of AABB
	}
	
	private boolean verifyStructure(int x1, int y1, int z1, int x2, int y2, int z2) {
		if (x2 <= x1 || y2 <= y1 || z2 <= z1) return false; // the structure is too small or invalid
		if (x2 - x1 - 1 > 3 || y2 - y1 - 1 > 3 || z2 - z1 - 1 > 3) return false; // the structure is too big
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				for (int z = z1; z <= z2; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					if (pos.equals(this.worldPosition)) continue;
					BlockState state = level.getBlockState(pos);
					if (x == x1 || x == x2 || y == y1 || y == y2 || z == z1 || z == z2) {
						if (!(state.getBlock() instanceof DistillChamberWallBlock)) return false;
					} else {
						if (!state.isAir()) return false;
					}
				}
			}
		}
		return true;
	}
	
	private int findEdge(BlockPos from, Direction side) {
		int i = 0;
		while (level.getBlockState(from.relative(side, i + 1)).getBlock() instanceof DistillChamberWallBlock) i++;
		return i;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		ListTag fuel = nbt.getList(tagFuel, CompoundTag.TAG_COMPOUND);
		this.fuel.clear();
		for (int i = 0; i < fuel.size(); i++) {
			CompoundTag item = fuel.getCompound(i);
			this.fuel.add(Pair.of(ItemStack.of(item.getCompound(tagItem)), item.getInt(tagValue)));
		}
		psi = nbt.getInt(tagPsi);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		var nbt = super.serializeNBT();
		ListTag list = new ListTag();
		for (Pair<ItemStack, Integer> pair : fuel) {
			CompoundTag item = new CompoundTag();
			item.put(tagItem, pair.getKey().save(new CompoundTag()));
			item.putInt(tagValue, pair.getValue());
			list.add(item);
		}
		nbt.put(tagFuel, list);
		nbt.putInt(tagPsi, psi);
		return nbt;
	}
	
}
