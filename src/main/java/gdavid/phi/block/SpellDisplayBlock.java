package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.SpellDisplayTile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.core.handler.PsiSoundHandler;
import vazkii.psi.common.item.ItemSpellDrive;

public class SpellDisplayBlock extends DirectionalBlock implements EntityBlock {
	
	public static final String id = "spell_display";
	
	private static final Map<Direction, VoxelShape> shapes;
	
	// TODO fix upside down texture with horizontal facing
	
	static {
		shapes = new HashMap<>();
		shapes.put(Direction.UP, Block.box(0, 0, 0, 16, 12, 16));
		shapes.put(Direction.DOWN, Block.box(0, 4, 0, 16, 16, 16));
		shapes.put(Direction.NORTH, Block.box(0, 0, 4, 16, 16, 16));
		shapes.put(Direction.SOUTH, Block.box(0, 0, 0, 16, 16, 12));
		shapes.put(Direction.WEST, Block.box(4, 0, 0, 16, 16, 16));
		shapes.put(Direction.EAST, Block.box(0, 0, 0, 12, 16, 16));
	}
	
	public SpellDisplayBlock() {
		super(Properties.of(Material.METAL).strength(5, 10).sound(SoundType.METAL).noOcclusion());
		registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip,
			TooltipFlag advanced) {
		tooltip.add(Component.translatable("block." + Phi.modId + "." + id + ".desc"));
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult rayTraceResult) {
		ItemStack item = player.getItemInHand(hand);
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof SpellDisplayTile)) return InteractionResult.PASS;
		Spell spell = ((SpellDisplayTile) tile).getSpell();
		if (spell == null) return InteractionResult.PASS;
		if (item.getItem() instanceof ItemSpellDrive) {
			if (!world.isClientSide) {
				world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, PsiSoundHandler.bulletCreate, SoundSource.BLOCKS, 0.5f, 1);
				ItemSpellDrive.setSpell(item, spell);
			}
		} else {
			if (!ISpellAcceptor.isAcceptor(item)) return InteractionResult.PASS;
			ISpellAcceptor acceptor = ISpellAcceptor.acceptor(item);
			if (!world.isClientSide) {
				world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, PsiSoundHandler.bulletCreate, SoundSource.BLOCKS, 0.5f, 1);
				acceptor.setSpell(player, spell);
			}
		}
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shapes.get(state.getValue(FACING));
	}
	
	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SpellDisplayTile(pos, state);
	}
	
}
