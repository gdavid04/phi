package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.CableTile;
import gdavid.phi.cable.CableNetwork;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class CableBlock extends Block implements EntityBlock {
	
	public static final String id = "cable";
	
	public static final BooleanProperty online = BooleanProperty.create("online");
	public static final Map<Direction, EnumProperty<CableSide>> sides = new HashMap<>();
	
	static {
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			sides.put(dir, EnumProperty.create(dir.getSerializedName(), CableSide.class));
		}
	}
	
	static final VoxelShape shape = Shapes.box(0, 0, 0, 1, 0.125f, 1);
	
	public CableBlock() {
		super(Properties.of(Material.DECORATION).instabreak().sound(SoundType.WOOD)
				.noCollission());
		BlockState state = getStateDefinition().any().setValue(online, false);
		for (EnumProperty<CableSide> side : sides.values()) {
			state = state.setValue(side, CableSide.none);
		}
		registerDefaultState(state);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip,
			TooltipFlag advanced) {
		tooltip.add(Component.translatable("block." + Phi.modId + ".cable.desc"));
	}
	
	// TODO hitboxes
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return shape;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(online);
		for (EnumProperty<CableSide> side : sides.values())
			builder.add(side);
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return canSupportCenter(world, pos.below(), Direction.UP);
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world,
			BlockPos pos, BlockPos facingPos) {
		if (facing == Direction.DOWN && !canSurvive(state, world, pos)) return Blocks.AIR.defaultBlockState();
		return state;
	}
	
	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!world.isClientSide && oldState.getBlock() != this) CableNetwork.rebuild(world, pos);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean flag) {
		super.onRemove(state, world, pos, newState, flag);
		if (!world.isClientSide && newState.getBlock() != this) CableNetwork.rebuild(world, pos);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CableTile(pos, state);
	}
	
	public enum CableSide implements StringRepresentable {
		
		none, side, up;
		
		@Override
		public String getSerializedName() {
			return name();
		}
		
	}
	
}
