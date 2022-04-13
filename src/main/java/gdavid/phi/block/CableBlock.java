package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.CableTile;
import gdavid.phi.util.ICableConnected;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CableBlock extends Block {
	
	public static final String id = "cable";
	
	public static final BooleanProperty online = BooleanProperty.create("online");
	public static final Map<Direction, EnumProperty<ConnectionState>> sides = new HashMap<>();
	
	static {
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			sides.put(dir, EnumProperty.create(dir.getString(), ConnectionState.class));
		}
	}
	
	static final VoxelShape shape = VoxelShapes.create(0, 0, 0, 1, 0.125f, 1);
	
	public CableBlock() {
		super(Properties.create(Material.MISCELLANEOUS).zeroHardnessAndResistance()
				.sound(SoundType.WOOD).doesNotBlockMovement());
		setRegistryName(id);
		BlockState state = getStateContainer().getBaseState().with(online, false);
		for (EnumProperty<ConnectionState> side : sides.values()) {
			state = state.with(side, ConnectionState.none);
		}
		setDefaultState(state);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, IBlockReader world, List<ITextComponent> tooltip,
			ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("block." + Phi.modId + ".cable.desc"));
	}
	
	// TODO hitboxes
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return shape;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(online);
		for (EnumProperty<ConnectionState> side : sides.values()) builder.add(side);
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		return hasEnoughSolidSide(world, pos.down(), Direction.UP);
	}
	
	public BlockState adjustConnections(BlockState state, IWorld world, BlockPos pos) {
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			state = adjustConnection(state, dir, world, pos);
		}
		return state;
	}
	
	public BlockState adjustConnection(BlockState state, Direction dir, IWorld world, BlockPos pos) {
		if (!Direction.Plane.HORIZONTAL.test(dir)) return state;
		BlockPos opos = pos.offset(dir);
		TileEntity otile = world.getTileEntity(opos);
		if (state.get(sides.get(dir)) == ConnectionState.none) {
			if (otile instanceof ICableConnected && ((ICableConnected) otile)
					.connectsInDirection(dir.getOpposite())) {
				state = connect(pos, state, world, dir);
			}
		} else if (!(otile instanceof ICableConnected) || !((ICableConnected) otile)
				.connectsInDirection(dir.getOpposite())) {
			state = disconnect(pos, state, world, dir);
		}
		return state;
	}
	
	public BlockState connect(BlockPos pos, BlockState state, IWorld world, Direction dir) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof CableTile) {
			if (((CableTile) tile).connect(dir)) state = world.getBlockState(pos);
			else state = state.with(sides.get(dir), ConnectionState.offline);
		}
		return state;
	}
	
	public BlockState disconnect(BlockPos pos, BlockState state, IWorld world, Direction dir) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof CableTile) {
			((CableTile) tile).disconnect(dir);
			state = state.with(online, ((CableTile) tile).connected != null);
		}
		state = state.with(sides.get(dir), ConnectionState.none);
		return state;
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		if (facing == Direction.DOWN && !isValidPosition(state, world, pos)) return Blocks.AIR.getDefaultState();
		return world.isRemote() ? state : adjustConnection(state, facing, world, pos);
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!world.isRemote) world.setBlockState(pos, adjustConnections(state, world, pos));
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CableTile();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	public enum ConnectionState implements IStringSerializable {
		
		none, offline, online;
		
		@Override
		public String getString() {
			return name();
		}
		
	}
	
}
