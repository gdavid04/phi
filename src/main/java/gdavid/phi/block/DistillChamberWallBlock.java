package gdavid.phi.block;

import gdavid.phi.Phi;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class DistillChamberWallBlock extends Block {
	
	public static final String id = "distill_chamber_wall";
	
	public DistillChamberWallBlock() {
		super(Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).strength(5, 10).sound(SoundType.METAL));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(
			ItemStack stack, BlockGetter world, List<Component> tooltip,
			TooltipFlag advanced) {
		tooltip.add(Component.translatable("block." + Phi.modId + "." + id + ".desc"));
	}
	
}
