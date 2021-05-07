package gdavid.phi.util;

import gdavid.phi.spell.operator.SplitVectorOperator;
import gdavid.phi.spell.other.ClockwiseConnector;
import gdavid.phi.spell.other.InOutConnector;
import net.minecraft.util.math.BlockPos;
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
		if (res < 0) {
			throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, piece.x, piece.y);
		}
		return res;
	}
	
	public static double positive(SpellPiece piece, SpellParam<Number> param) throws SpellCompilationException {
		double res = piece.getNonNullParamEvaluation(param).doubleValue();
		if (res <= 0) {
			throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, piece.x, piece.y);
		}
		return res;
	}
	
	public static Vector3 nonNull(SpellPiece piece, SpellContext context, SpellParam<Vector3> param)
			throws SpellRuntimeException {
		Vector3 res = piece.getNonnullParamValue(context, param);
		if (res.isZero()) {
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		}
		return res;
	}
	
	public static Vector3 inRange(SpellPiece piece, SpellContext context, SpellParam<Vector3> param)
			throws SpellRuntimeException {
		Vector3 res = nonNull(piece, context, param);
		if (!context.isInRadius(res)) {
			throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
		}
		return res;
	}
	
	public static BlockPos block(SpellPiece piece, SpellContext context, SpellParam<Vector3> param)
			throws SpellRuntimeException {
		return inRange(piece, context, param).toBlockPos();
	}
	
	public static boolean checkSide(SpellPiece piece, Side side) {
		if (piece == null) {
			return false;
		}
		// TODO move these to piece classes
		if (piece instanceof ClockwiseConnector) {
			// No recursive check to avoid dealing with infinite loops
			return piece.spell.grid.getPieceAtSideSafely(piece.x, piece.y,
					((ClockwiseConnector) piece).reverseSide(side.getOpposite())) != null;
		} else if (piece instanceof InOutConnector) {
			InOutConnector connector = (InOutConnector) piece;
			return connector.paramSides.get(connector.from) == side
					|| connector.paramSides.get(connector.bidir) == side;
		} else if (piece instanceof SplitVectorOperator) {
			return piece.paramSides.get(((SplitVectorOperator) piece).vector) == side;
		}
		for (Side param : piece.paramSides.values()) {
			if (param == side) {
				return true;
			}
		}
		return false;
	}
	
}
