package gdavid.phi.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

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
	
	public static float[] colorOrColorizer(SpellPiece piece, SpellContext context, SpellParam<Vector3> param) throws SpellRuntimeException {
		Vector3 colorVal = piece.getParamValue(context, param);
		if (colorVal != null) return new float[] {
			(float) Math.max(0, Math.min(1, colorVal.x)),
			(float) Math.max(0, Math.min(1, colorVal.y)),
			(float) Math.max(0, Math.min(1, colorVal.z))
		};
		ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
		ItemStack colorizer = cad == null ? ItemStack.EMPTY : ((ICAD) cad.getItem()).getComponentInSlot(cad, EnumCADComponent.DYE);
		int col = getColorForColorizer(colorizer);
		return new float[] { r(col) / 255.0f, g(col) / 255.0f, b(col) / 255.0f };
	}
	
}
