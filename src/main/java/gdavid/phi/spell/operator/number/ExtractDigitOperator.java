package gdavid.phi.spell.operator.number;

import java.math.BigDecimal;

import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class ExtractDigitOperator extends PieceOperator {
	
	SpellParam<Number> number, digit, base;
	
	public ExtractDigitOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(number = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.GREEN, false, false));
		addParam(digit = new ParamNumber(Param.digit.name, SpellParam.RED, false, false));
		addParam(base = new ParamNumber(SpellParam.GENERIC_NAME_BASE, SpellParam.BLUE, true, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		double num = getParamValue(context, number).doubleValue();
		int d = getParamValue(context, digit).intValue();
		double b = getParamValueOrDefault(context, base, 10).doubleValue();
		if (d <= 0) Errors.runtime(SpellRuntimeException.NON_POSITIVE_VALUE);
		if (b <= 1) Errors.runtime(SpellRuntimeException.INVALID_BASE);
		BigDecimal bd = BigDecimal.valueOf(b);
		return BigDecimal.valueOf(num).divideToIntegralValue(bd.pow(d - 1)).remainder(bd).intValue();
	}
	
}
