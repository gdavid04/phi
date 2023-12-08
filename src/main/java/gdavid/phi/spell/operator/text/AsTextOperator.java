package gdavid.phi.spell.operator.text;

import gdavid.phi.spell.Errors;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Any;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;
import vazkii.psi.api.spell.piece.PieceOperator;

public class AsTextOperator extends PieceOperator {
	
	SpellParam<Any> value;
	
	public AsTextOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(value = new ParamAny(SpellParam.GENERIC_NAME_TARGET, SpellParam.BLUE, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Object val = getRawParamValue(context, value);
		if (val == null) Errors.runtime(SpellRuntimeException.NULL_TARGET);
		return val.toString();
	}
	
}
