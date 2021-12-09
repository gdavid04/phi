package gdavid.phi.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class ReplaceVectorComponentOperator extends PieceOperator {
	
	SpellParam<Vector3> vector;
	SpellParam<Number> x, y, z;
	
	public ReplaceVectorComponentOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(vector = new ParamVector(SpellParam.GENERIC_NAME_VECTOR, SpellParam.GRAY, false, false));
		addParam(x = new ParamNumber(SpellParam.GENERIC_NAME_X, SpellParam.RED, true, false));
		addParam(y = new ParamNumber(SpellParam.GENERIC_NAME_Y, SpellParam.GREEN, true, false));
		addParam(z = new ParamNumber(SpellParam.GENERIC_NAME_Z, SpellParam.BLUE, true, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Vector3.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 vec = getNonnullParamValue(context, vector);
		return new Vector3(getParamValueOrDefault(context, x, vec.x).doubleValue(),
				getParamValueOrDefault(context, y, vec.y).doubleValue(),
				getParamValueOrDefault(context, z, vec.z).doubleValue());
	}
	
}
