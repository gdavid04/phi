package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.MPUTile;
import gdavid.phi.block.tile.PsimetalCrusherTile;
import gdavid.phi.cable.CableNetwork;
import gdavid.phi.util.RedstoneMode;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MPUBlock extends HorizontalDirectionalBlock implements EntityBlock {
	
	public static final String id = "mpu";
	
	public MPUBlock() {
		super(Properties.of(Material.METAL).strength(5, 10).sound(SoundType.METAL).noOcclusion());
		registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip,
			TooltipFlag advanced) {
		tooltip.add(Component.translatable("block." + Phi.modId + ".mpu.desc"));
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult rayTraceResult) {
		ItemStack item = player.getItemInHand(hand);
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof MPUTile)) return InteractionResult.PASS;
		if (item.getItem() == Items.REDSTONE_TORCH) {
			((MPUTile) tile).redstoneMode = RedstoneMode.values()[(((MPUTile) tile).redstoneMode.ordinal() + 1)
					% RedstoneMode.values().length];
			player.displayClientMessage(
					Component.translatable(Phi.modId + ".redstone_mode." + ((MPUTile) tile).redstoneMode), true);
			return InteractionResult.SUCCESS;
		}
		Class<?> spellDrive = null;
		try {
			spellDrive = Class.forName("vazkii.psi.common.item.ItemSpellDrive");
			if (!(boolean) Class.forName("vazkii.psi.common.item.ItemCAD").getMethod("isTruePlayer", Entity.class)
					.invoke(null, player)) {
				return InteractionResult.PASS;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Spell spell = null;
		if (spellDrive != null && spellDrive.isInstance(item.getItem())) {
			try {
				spell = (Spell) spellDrive.getMethod("getSpell", ItemStack.class).invoke(item.getItem(), item);
			} catch (Exception e) {
				e.printStackTrace();
				return InteractionResult.PASS;
			}
		} else {
			if (!ISpellAcceptor.isAcceptor(item)) return InteractionResult.PASS;
			ISpellAcceptor acceptor = ISpellAcceptor.acceptor(item);
			if (!acceptor.containsSpell()) return InteractionResult.PASS;
			spell = acceptor.getSpell();
		}
		if (!world.isClientSide && spell != null) {
			((MPUTile) tile).setSpell(spell);
		}
		return InteractionResult.SUCCESS;
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
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!world.isClientSide) CableNetwork.rebuild(world, pos);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean flag) {
		super.onRemove(state, world, pos, newState, flag);
		if (!world.isClientSide) CableNetwork.rebuild(world, pos);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MPUTile(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return type != MPUTile.type ? null : (level, pos, state1, tile) -> ((MPUTile) tile).tick();
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof MPUTile)) return 0;
		return ((MPUTile) tile).comparatorSignal;
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
}
