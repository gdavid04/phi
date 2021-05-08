package gdavid.phi.spell.operator.number;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class ToDegreesOperator extends PieceOperator {
	
	SpellParam<Number> num;
	
	public ToDegreesOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(num = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.GREEN, false, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return Math.toDegrees(getParamValue(context, num).doubleValue());
	}
	
}
