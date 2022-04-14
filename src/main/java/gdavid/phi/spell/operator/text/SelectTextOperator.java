package gdavid.phi.spell.operator.text;

import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.TextParam;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class SelectTextOperator extends PieceOperator {
	
	SpellParam<String> positive, negative, def;
	SpellParam<Number> condition;
	
	public SelectTextOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(positive = new TextParam(Param.positive.name, SpellParam.GREEN, true, false));
		addParam(negative = new TextParam(Param.negative.name, SpellParam.RED, true, false));
		addParam(def = new TextParam(Param.def.name, SpellParam.GRAY, true, false));
		addParam(condition = new ParamNumber(Param.condition.name, SpellParam.BLUE, false, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Double cond = getParamValue(context, condition).doubleValue();
		SpellParam<String> param = def;
		if (cond > 0) param = positive;
		else if (cond < 0) param = negative;
		if (!paramSides.get(param).isEnabled()) {
			if (paramSides.get(def).isEnabled()) return getParamValue(context, def);
			Errors.runtime(SpellRuntimeException.NULL_TARGET);
		}
		return getParamValue(context, param);
	}
	
}
