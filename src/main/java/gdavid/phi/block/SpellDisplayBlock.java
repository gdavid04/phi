package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.SpellDisplayTile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.core.handler.PsiSoundHandler;
import vazkii.psi.common.item.ItemSpellDrive;

public class SpellDisplayBlock extends DirectionalBlock {
	
	public static final String id = "spell_display";
	
	private static final Map<Direction, VoxelShape> shapes;
	
	// TODO fix upside down texture with horizontal facing
	
	static {
		shapes = new HashMap<>();
		shapes.put(Direction.UP, Block.makeCuboidShape(0, 0, 0, 16, 12, 16));
		shapes.put(Direction.DOWN, Block.makeCuboidShape(0, 4, 0, 16, 16, 16));
		shapes.put(Direction.NORTH, Block.makeCuboidShape(0, 0, 4, 16, 16, 16));
		shapes.put(Direction.SOUTH, Block.makeCuboidShape(0, 0, 0, 16, 16, 12));
		shapes.put(Direction.WEST, Block.makeCuboidShape(4, 0, 0, 16, 16, 16));
		shapes.put(Direction.EAST, Block.makeCuboidShape(0, 0, 0, 12, 16, 16));
	}
	
	public SpellDisplayBlock() {
		super(Properties.create(Material.IRON).hardnessAndResistance(5, 10).sound(SoundType.METAL).notSolid());
		setRegistryName(id);
		setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, IBlockReader world, List<ITextComponent> tooltip,
			ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("block." + Phi.modId + "." + id + ".desc"));
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand hand, BlockRayTraceResult rayTraceResult) {
		ItemStack item = player.getHeldItem(hand);
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof SpellDisplayTile)) return ActionResultType.PASS;
		Spell spell = ((SpellDisplayTile) tile).getSpell();
		if (spell == null) return ActionResultType.PASS;
		if (item.getItem() instanceof ItemSpellDrive) {
			if (!world.isRemote) {
				world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, PsiSoundHandler.bulletCreate, SoundCategory.BLOCKS, 0.5f, 1);
				ItemSpellDrive.setSpell(item, spell);
			}
		} else {
			if (!ISpellAcceptor.isAcceptor(item)) return ActionResultType.PASS;
			ISpellAcceptor acceptor = ISpellAcceptor.acceptor(item);
			if (!world.isRemote) {
				world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, PsiSoundHandler.bulletCreate, SoundCategory.BLOCKS, 0.5f, 1);
				acceptor.setSpell(player, spell);
			}
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return shapes.get(state.get(FACING));
	}
	
	@Override
	public boolean isTransparent(BlockState state) {
		return true;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getFace());
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SpellDisplayTile();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
}
