package gdavid.phi.block;

import java.util.Random;

import gdavid.phi.Phi;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;

public class ShadowBlock extends AirBlock {
	
	// TODO allow waterlogging
	
	public static final String id = "shadow";
	
	@ObjectHolder(Phi.modId + ":" + id)
	public static ShadowBlock instance;
	
	public ShadowBlock() {
		super(Properties.create(Material.AIR)
			.notSolid()
			.noDrops());
		setRegistryName(id);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return false;
	}
	
	@Override
	public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 8;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 0.6f;
	}
	
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		world.removeBlock(pos, false);
	}
	
}
