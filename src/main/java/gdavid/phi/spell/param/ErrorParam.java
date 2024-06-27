package gdavid.phi.spell.param;

import com.mojang.datafixers.util.Either;
import gdavid.phi.spell.error.PropagatingSpellRuntimeException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.param.ParamAny;

import vazkii.psi.api.spell.SpellParam.ArrowType;

/**
 * A spell parameter that accepts {@link PropagatingSpellRuntimeException}s
 */
public class ErrorParam extends ParamAny {
	
	public ErrorParam(String name, int color, boolean canDisable) {
		super(name, color, canDisable);
	}
	
	public ErrorParam(String name, int color, boolean canDisable, ArrowType arrowType) {
		super(name, color, canDisable, arrowType);
	}
	
	public Either<Object, PropagatingSpellRuntimeException> get(SpellPiece piece, SpellContext context) {
		Object value = piece.getRawParamValue(context, this);
		if (value instanceof PropagatingSpellRuntimeException) {
			return Either.right((PropagatingSpellRuntimeException) value);
		}
		return Either.left(value);
	}
	
}
