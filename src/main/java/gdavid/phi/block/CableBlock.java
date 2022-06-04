package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.CableTile;
import gdavid.phi.cable.CableNetwork;
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
	public static final Map<Direction, EnumProperty<CableSide>> sides = new HashMap<>();
	
	static {
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			sides.put(dir, EnumProperty.create(dir.getString(), CableSide.class));
		}
	}
	
	static final VoxelShape shape = VoxelShapes.create(0, 0, 0, 1, 0.125f, 1);
	
	public CableBlock() {
		super(Properties.create(Material.MISCELLANEOUS).zeroHardnessAndResistance().sound(SoundType.WOOD)
				.doesNotBlockMovement());
		setRegistryName(id);
		BlockState state = getStateContainer().getBaseState().with(online, false);
		for (EnumProperty<CableSide> side : sides.values()) {
			state = state.with(side, CableSide.none);
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
		for (EnumProperty<CableSide> side : sides.values())
			builder.add(side);
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		return hasEnoughSolidSide(world, pos.down(), Direction.UP);
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world,
			BlockPos pos, BlockPos facingPos) {
		if (facing == Direction.DOWN && !isValidPosition(state, world, pos)) return Blocks.AIR.getDefaultState();
		return state;
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!world.isRemote && oldState.getBlock() != this) CableNetwork.rebuild(world, pos);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean flag) {
		super.onReplaced(state, world, pos, newState, flag);
		if (!world.isRemote && newState.getBlock() != this) CableNetwork.rebuild(world, pos);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CableTile();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	public enum CableSide implements IStringSerializable {
		
		none, side, up;
		
		@Override
		public String getString() {
			return name();
		}
		
	}
	
}
