package gdavid.phi.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import vazkii.psi.api.cad.ICADColorizer;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
	
	public static int getColorForColorizer(ItemStack colorizer) {
		if (colorizer.getItem() instanceof ICADColorizer) {
			return ((ICADColorizer) colorizer.getItem()).getColor(colorizer);
		}
		return ICADColorizer.DEFAULT_SPELL_COLOR;
	}
	
	public static int r(int color) {
		return (color >> 16) & 0xFF;
	}
	
	public static int g(int color) {
		return (color >> 8) & 0xFF;
	}
	
	public static int b(int color) {
		return color & 0xFF;
	}
	
	public static int a(int color) {
		return (color >> 24) & 0xFF;
	}
	
}
