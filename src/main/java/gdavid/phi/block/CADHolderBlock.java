package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.CADHolderTile;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class CADHolderBlock extends HorizontalDirectionalBlock implements EntityBlock {
	
	// TODO cable compat
	
	public static final String id = "cad_holder";
	
	public CADHolderBlock() {
		super(Properties.of(Material.METAL).strength(5, 10).sound(SoundType.METAL).noOcclusion());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip,
			TooltipFlag advanced) {
		tooltip.add(Component.translatable("block." + Phi.modId + ".cad_holder.desc"));
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult rayTraceResult) {
		ItemStack item = player.getItemInHand(hand);
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof CADHolderTile)) return InteractionResult.PASS;
		CADHolderTile holder = (CADHolderTile) tile;
		if (holder.hasItem() == item.isEmpty() && !world.isClientSide) {
			if (holder.hasItem()) {
				player.setItemInHand(hand, holder.item);
				holder.removeItem();
			} else {
				holder.setItem(item);
				player.setItemInHand(hand, ItemStack.EMPTY);
			}
		}
		return InteractionResult.SUCCESS;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean flag) {
		if (!world.isClientSide && newState.getBlock() != this) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile instanceof CADHolderTile) {
				CADHolderTile holder = (CADHolderTile) tile;
				if (holder.hasItem()) {
					world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), holder.item));
				}
			}
		}
		super.onRemove(state, world, pos, newState, flag);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CADHolderTile(pos, state);
	}
	
}
