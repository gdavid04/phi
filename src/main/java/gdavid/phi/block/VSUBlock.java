package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.VSUTile;
import gdavid.phi.cable.CableNetwork;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import vazkii.psi.common.item.ItemVectorRuler;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class VSUBlock extends Block implements EntityBlock {
	
	public static final String id = "vsu";
	
	public VSUBlock() {
		super(Properties.of(Material.METAL).strength(5, 10).sound(SoundType.METAL).noOcclusion());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip,
			TooltipFlag advanced) {
		tooltip.add(Component.translatable("block." + Phi.modId + ".vsu.desc"));
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult rayTraceResult) {
		ItemStack item = player.getItemInHand(hand);
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof VSUTile)) return InteractionResult.PASS;
		if (!(item.getItem() instanceof ItemVectorRuler)) return InteractionResult.PASS;
		if (!world.isClientSide) {
			((VSUTile) tile).setVector(((ItemVectorRuler) item.getItem()).getVector(item));
		}
		return InteractionResult.SUCCESS;
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
		return new VSUTile(pos, state);
	}
	
}
