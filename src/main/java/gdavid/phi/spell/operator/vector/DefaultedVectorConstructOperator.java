package gdavid.phi.spell.operator.vector;

import gdavid.phi.spell.Param;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class DefaultedVectorConstructOperator extends PieceOperator {
	
	SpellParam<Number> x, y, z, def;
	
	public DefaultedVectorConstructOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(x = new ParamNumber(SpellParam.GENERIC_NAME_X, SpellParam.RED, true, false));
		addParam(y = new ParamNumber(SpellParam.GENERIC_NAME_Y, SpellParam.GREEN, true, false));
		addParam(z = new ParamNumber(SpellParam.GENERIC_NAME_Z, SpellParam.BLUE, true, false));
		addParam(def = new ParamNumber(Param.def.name, SpellParam.GRAY, true, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Vector3.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		double d = getParamValueOrDefault(context, def, 0).doubleValue();
		return new Vector3(getParamValueOrDefault(context, x, d).doubleValue(),
				getParamValueOrDefault(context, y, d).doubleValue(),
				getParamValueOrDefault(context, z, d).doubleValue());
	}
	
}
