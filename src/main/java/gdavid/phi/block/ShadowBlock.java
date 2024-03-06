package gdavid.phi.block;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShadowBlock extends AirBlock {
	
	public static final String id = "shadow";
	
	public ShadowBlock() {
		super(Properties.of(Material.AIR).noOcclusion().noLootTable());
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return false;
	}
	
	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 8;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0.6f;
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		world.removeBlock(pos, false);
	}
	
}
