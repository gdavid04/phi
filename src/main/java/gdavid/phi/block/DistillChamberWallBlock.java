package gdavid.phi.block;

import gdavid.phi.Phi;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class DistillChamberWallBlock extends Block {
	
	public static final String id = "distill_chamber_wall";
	
	public DistillChamberWallBlock() {
		super(Properties.create(Material.IRON).hardnessAndResistance(5, 10).sound(SoundType.METAL));
		setRegistryName(id);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(
			ItemStack stack, IBlockReader world, List<ITextComponent> tooltip,
			ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("block." + Phi.modId + "." + id + ".desc"));
	}
	
}
