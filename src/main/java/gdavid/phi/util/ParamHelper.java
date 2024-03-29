package gdavid.phi.util;

import gdavid.phi.spell.Errors;
import gdavid.phi.spell.param.ReferenceParam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ParamHelper {
	
	public static double positiveOrZero(SpellPiece piece, SpellParam<Number> param) throws SpellCompilationException {
		double res = piece.getNonNullParamEvaluation(param).doubleValue();
		if (res < 0) Errors.compile(SpellCompilationException.NON_POSITIVE_VALUE, piece.x, piece.y);
		return res;
	}
	
	public static double positive(SpellPiece piece, SpellParam<Number> param) throws SpellCompilationException {
		double res = piece.getNonNullParamEvaluation(param).doubleValue();
		if (res <= 0) Errors.compile(SpellCompilationException.NON_POSITIVE_VALUE, piece.x, piece.y);
		return res;
	}
	
	public static Vector3 nonNull(SpellPiece piece, SpellContext context, SpellParam<Vector3> param)
			throws SpellRuntimeException {
		Vector3 res = piece.getNonnullParamValue(context, param);
		if (res.isZero()) Errors.runtime(SpellRuntimeException.NULL_VECTOR);
		return res;
	}
	
	public static Vector3 inRange(SpellPiece piece, SpellContext context, SpellParam<Vector3> param)
			throws SpellRuntimeException {
		Vector3 res = nonNull(piece, context, param);
		if (!context.isInRadius(res)) Errors.runtime(SpellRuntimeException.OUTSIDE_RADIUS);
		return res;
	}
	
	public static BlockPos block(SpellPiece piece, SpellContext context, SpellParam<Vector3> param)
			throws SpellRuntimeException {
		return inRange(piece, context, param).toBlockPos();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static int connectorColor(SpellPiece piece, Side side, int def) {
		// replaced by Psionic Utilities
		return def;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void outputTooltip(SpellPiece piece, Consumer<List<ITextComponent>> superFn, List<ITextComponent> tooltip) {
		Map<SpellParam<?>, Side> paramSidesTmp = new HashMap<>(piece.paramSides);
		piece.paramSides.keySet().removeIf(e -> e instanceof ReferenceParam && ((ReferenceParam) e).isOutput);
		superFn.accept(tooltip);
		piece.paramSides.putAll(paramSidesTmp);
		for (SpellParam<?> param : piece.paramSides.keySet()) {
			if (param instanceof ReferenceParam && ((ReferenceParam) param).isOutput) {
				ITextComponent name = new TranslationTextComponent(param.name).mergeStyle(TextFormatting.YELLOW);
				ITextComponent type = new StringTextComponent(" [").append(param.getRequiredTypeString()).appendString("]").mergeStyle(TextFormatting.YELLOW);
				tooltip.add((new StringTextComponent(param.canDisable ? "[Output] " : " Output  ")).append(name).append(type));
			}
		}
	}
	
}
