package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.SpellStorageTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class SpellStorageBlock extends Block implements EntityBlock {
	
	public static final String id = "spell_storage";
	
	public SpellStorageBlock() {
		super(Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).strength(5, 10).sound(SoundType.METAL).noOcclusion());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip,
			TooltipFlag advanced) {
		tooltip.add(Component.translatable("block." + Phi.modId + "." + id + ".desc"));
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SpellStorageTile(pos, state);
	}
	
}
