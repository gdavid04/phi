package gdavid.phi.block;

import gdavid.phi.Phi;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class DistillChamberWallBlock extends Block {
	
	public static final String id = "distill_chamber_wall";
	
	public DistillChamberWallBlock() {
		super(Properties.of(Material.METAL).strength(5, 10).sound(SoundType.METAL));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(
			ItemStack stack, BlockGetter world, List<Component> tooltip,
			TooltipFlag advanced) {
		tooltip.add(Component.translatable("block." + Phi.modId + "." + id + ".desc"));
	}
	
}
