package gdavid.phi.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class ClampVectorOperator extends PieceOperator {
	
	SpellParam<Vector3> vector;
	SpellParam<Number> max;
	
	public ClampVectorOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(vector = new ParamVector(SpellParam.GENERIC_NAME_VECTOR, SpellParam.GREEN, false, false));
		addParam(max = new ParamNumber(SpellParam.GENERIC_NAME_MAX, SpellParam.RED, false, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Vector3.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 vec = getNonnullParamValue(context, vector);
		double maxLength = getNonnullParamValue(context, max).doubleValue();
		if (maxLength < 0) {
			throw new SpellRuntimeException(SpellRuntimeException.NEGATIVE_NUMBER);
		}
		if (vec.magSquared() > maxLength * maxLength) {
			return vec.copy().normalize().multiply(maxLength);
		}
		return vec;
	}
	
}
