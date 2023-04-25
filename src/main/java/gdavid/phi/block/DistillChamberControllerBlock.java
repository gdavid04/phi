package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.DistillChamberControllerTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class DistillChamberControllerBlock extends DirectionalBlock {
	
	public static final String id = "distill_chamber_controller";
	
	public DistillChamberControllerBlock() {
		super(Properties.create(Material.IRON).hardnessAndResistance(5, 10).sound(SoundType.METAL));
		setRegistryName(id);
		setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(
			ItemStack stack, IBlockReader world, List<ITextComponent> tooltip,
			ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("block." + Phi.modId + "." + id + ".desc"));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new DistillChamberControllerTile();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
}
