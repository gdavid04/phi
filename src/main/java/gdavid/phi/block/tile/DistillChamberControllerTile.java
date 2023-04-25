package gdavid.phi.block.tile;

import gdavid.phi.block.DistillChamberControllerBlock;
import gdavid.phi.block.DistillChamberWallBlock;
import gdavid.phi.entity.PsiProjectileEntity;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;
import vazkii.psi.common.lib.ModTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistillChamberControllerTile extends TileEntity implements ITickableTileEntity {
	
	public static TileEntityType<DistillChamberControllerTile> type;
	
	public static final String tagFuel = "fuel";
	public static final String tagItem = "item";
	public static final String tagValue = "value";
	public static final String tagPsi = "psi";
	
	private static final int storagePerBlock = 4;
	
	private List<Pair<ItemStack, Integer>> fuel = new ArrayList<>();
	private int psi = 0;
	
	public DistillChamberControllerTile() {
		super(type);
	}
	
	@Override
	public void tick() {
		int size = getStructureVolume();
		producePsi(size);
		if (psi >= 100) {
			PsiProjectileEntity projectile = new PsiProjectileEntity(world, Vector3d.copy(getBlockState().get(DistillChamberControllerBlock.FACING).getDirectionVec()), psi);
			projectile.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			projectile.setOrigin();
			world.addEntity(projectile);
			psi = 0;
			markDirty();
		}
		if (fuel.size() < size * storagePerBlock) {
			ItemEntity item = getFuelItem();
			System.out.println(item);
			if (item != null) {
				ItemStack stack = item.getItem();
				ItemStack fuelStack = stack.split(1);
				fuel.add(Pair.of(fuelStack, getValue(fuelStack)));
				if (stack.isEmpty()) item.remove();
				else item.setItem(stack);
				markDirty();
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
		if (changed) markDirty();
	}
	
	private ItemEntity getFuelItem() {
		AxisAlignedBB aabb = AxisAlignedBB.withSizeAtOrigin(1, 1, 1).offset(Vector3d.copyCentered(pos.offset(getBlockState().get(DistillChamberControllerBlock.FACING))));
		List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, aabb);
		return items.stream().filter(item -> item.getItem().getItem().isIn(ModTags.PSIDUST)).findAny().orElse(null);
	}
	
	private int getValue(ItemStack stack) {
		if (stack.getItem().isIn(ModTags.PSIDUST)) return 625;
		return 0;
	}
	
	private int getStructureVolume() {
		Direction dir = getBlockState().get(DistillChamberControllerBlock.FACING).getOpposite(); // interior direction
		Map<Direction, Integer> edges = new HashMap<>(6);
		BlockPos edgePos = null;
		for (Direction side : Direction.values()) { // determine the size of the face the controller is on
			if (side.getAxis() == dir.getAxis()) continue;
			int i = findEdge(pos, side);
			int opos = side.getAxis().getCoordinate(pos.getX(), pos.getY(), pos.getZ());
			if (side.getAxisDirection() == Direction.AxisDirection.POSITIVE) opos += i;
			else opos -= i;
			edges.put(side, opos);
			if (edgePos == null) edgePos = pos.offset(side, i * side.getAxisDirection().getOffset());
		}
		int depth = findEdge(edgePos, dir); // determine depth at an edge
		edges.put(dir, dir.getAxis().getCoordinate(pos.getX(), pos.getY(), pos.getZ()) + depth * dir.getAxisDirection().getOffset());
		edges.put(dir.getOpposite(), dir.getAxis().getCoordinate(pos.getX(), pos.getY(), pos.getZ()));
		int x1 = edges.get(Direction.getFacingFromAxis(Direction.AxisDirection.NEGATIVE, Axis.X));
		int y1 = edges.get(Direction.getFacingFromAxis(Direction.AxisDirection.NEGATIVE, Axis.Y));
		int z1 = edges.get(Direction.getFacingFromAxis(Direction.AxisDirection.NEGATIVE, Axis.Z));
		int x2 = edges.get(Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, Axis.X));
		int y2 = edges.get(Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, Axis.Y));
		int z2 = edges.get(Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, Axis.Z));
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
					if (pos.equals(this.pos)) continue;
					BlockState state = world.getBlockState(pos);
					if (x == x1 || x == x2 || y == y1 || y == y2 || z == z1 || z == z2) {
						if (!(state.getBlock() instanceof DistillChamberWallBlock)) return false;
					} else {
						if (!state.getBlock().isAir(state, world, pos)) return false;
					}
				}
			}
		}
		return true;
	}
	
	private int findEdge(BlockPos from, Direction side) {
		int i = 0;
		while (world.getBlockState(from.offset(side, i + 1)).getBlock() instanceof DistillChamberWallBlock) i++;
		return i;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		ListNBT fuel = nbt.getList(tagFuel, Constants.NBT.TAG_COMPOUND);
		this.fuel.clear();
		for (int i = 0; i < fuel.size(); i++) {
			CompoundNBT item = fuel.getCompound(i);
			this.fuel.add(Pair.of(ItemStack.read(item.getCompound(tagItem)), item.getInt(tagValue)));
		}
		psi = nbt.getInt(tagPsi);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		ListNBT list = new ListNBT();
		for (Pair<ItemStack, Integer> pair : fuel) {
			CompoundNBT item = new CompoundNBT();
			item.put(tagItem, pair.getKey().write(new CompoundNBT()));
			item.putInt(tagValue, pair.getValue());
			list.add(item);
		}
		nbt.put(tagFuel, list);
		nbt.putInt(tagPsi, psi);
		return nbt;
	}
	
}
