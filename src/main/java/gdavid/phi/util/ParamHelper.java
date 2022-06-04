package gdavid.phi.util;

import gdavid.phi.spell.Errors;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

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
	
	public static int positiveInt(SpellPiece piece, SpellParam<Number> param) throws SpellCompilationException {
		double res = piece.getNonNullParamEvaluation(param).doubleValue();
		if (res <= 0 || res != (int) res) Errors.compile(SpellCompilationException.NON_POSITIVE_INTEGER, piece.x, piece.y);
		return (int) res;
	}
	
	public static int positiveInt(SpellPiece piece, SpellParam<Number> param, int def) throws SpellCompilationException {
		double res = piece.getParamEvaluationeOrDefault(param, def).doubleValue();
		if (res <= 0 || res != (int) res) Errors.compile(SpellCompilationException.NON_POSITIVE_INTEGER, piece.x, piece.y);
		return (int) res;
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
	
}
