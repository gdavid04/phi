package gdavid.phi.spell.operator.number;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class DifferenceOperator extends PieceOperator {
	
	SpellParam<Number> a, b;
	
	public DifferenceOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(a = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER1, SpellParam.GREEN, false, false));
		addParam(b = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER2, SpellParam.GREEN, false, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return Math.abs(getParamValue(context, a).doubleValue() - getParamValue(context, b).doubleValue());
	}
	
}
