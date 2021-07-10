package gdavid.phi.util;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
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
	
	public static boolean isLoop(SpellPiece piece) {
		return isLoop(piece, new HashSet<>());
	}
	
	public static boolean isLoop(SpellPiece piece, Set<SpellPiece> visited) {
		if (piece == null) return false;
		if (visited.contains(piece)) return true;
		visited.add(piece);
		for (Entry<SpellParam<?>, Side> param : piece.paramSides.entrySet()) {
			if (param.getKey() instanceof ReferenceParam) continue;
			if (!param.getValue().isEnabled()) continue;
			try {
				SpellPiece other = piece.spell.grid.getPieceAtSideWithRedirections(piece.x, piece.y, param.getValue());
				if (isLoop(other, new HashSet<>(visited))) return true;
			} catch (SpellCompilationException e) {
				return true;
			}
		}
		return false;
	}
	
}
