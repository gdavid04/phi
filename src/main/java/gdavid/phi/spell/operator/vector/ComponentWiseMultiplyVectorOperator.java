package gdavid.phi.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class ComponentWiseMultiplyVectorOperator extends PieceOperator {
	
	SpellParam<Vector3> a, b, c;
	
	public ComponentWiseMultiplyVectorOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(a = new ParamVector(SpellParam.GENERIC_NAME_VECTOR1, SpellParam.GREEN, false, false));
		addParam(b = new ParamVector(SpellParam.GENERIC_NAME_VECTOR2, SpellParam.GREEN, false, false));
		addParam(c = new ParamVector(SpellParam.GENERIC_NAME_VECTOR2, SpellParam.GREEN, true, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return getParamValue(context, a).copy().multiply(getParamValue(context, b))
				.multiply(getParamValueOrDefault(context, c, Vector3.one));
	}
	
}
