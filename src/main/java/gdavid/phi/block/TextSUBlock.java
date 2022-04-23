package gdavid.phi.block;

import java.util.List;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.TextSUTile;
import gdavid.phi.cable.CableNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TextSUBlock extends Block {
	
	public static final String id = "textsu";
	
	public TextSUBlock() {
		super(Properties.create(Material.IRON).hardnessAndResistance(5, 10).sound(SoundType.METAL).notSolid());
		setRegistryName(id);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, IBlockReader world, List<ITextComponent> tooltip,
			ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("block." + Phi.modId + ".textsu.desc"));
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!world.isRemote) CableNetwork.rebuild(world, pos);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean flag) {
		super.onReplaced(state, world, pos, newState, flag);
		if (!world.isRemote) CableNetwork.rebuild(world, pos);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TextSUTile();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
}
