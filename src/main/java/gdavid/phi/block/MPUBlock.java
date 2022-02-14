package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.MPUTile;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.ItemSpellDrive;

public class MPUBlock extends HorizontalBlock {
	
	public static final String id = "mpu";
	
	public MPUBlock() {
		super(Properties.create(Material.IRON).hardnessAndResistance(5, 10).sound(SoundType.METAL).notSolid());
		setRegistryName(id);
		setDefaultState(getStateContainer().getBaseState().with(HORIZONTAL_FACING, Direction.NORTH));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, IBlockReader world, List<ITextComponent> tooltip,
			ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("block." + Phi.modId + ".mpu.desc"));
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand hand, BlockRayTraceResult rayTraceResult) {
		ItemStack item = player.getHeldItem(hand);
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof MPUTile) || !ItemCAD.isTruePlayer(player)) return ActionResultType.PASS;
		Spell spell;
		if (item.getItem() instanceof ItemSpellDrive) {
			spell = ItemSpellDrive.getSpell(item);
		} else {
			if (!ISpellAcceptor.isAcceptor(item)) return ActionResultType.PASS;
			ISpellAcceptor acceptor = ISpellAcceptor.acceptor(item);
			if (!acceptor.containsSpell()) return ActionResultType.PASS;
			spell = acceptor.getSpell();
		}
		if (!world.isRemote && spell != null) {
			((MPUTile) tile).setSpell(spell);
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new MPUTile();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof MPUTile)) return 0;
		return ((MPUTile) tile).comparatorSignal;
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
}
