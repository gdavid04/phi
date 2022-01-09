package gdavid.phi.spell.operator.number;

import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.TextParam;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceOperator;

public class NumberFromTextOperator extends PieceOperator {
	
	SpellParam<String> text;
	
	public NumberFromTextOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(text = new TextParam(Param.text.name, SpellParam.BLUE, false, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		try {
			return Double.parseDouble(getParamValue(context, text));
		} catch (NumberFormatException e) {
			Errors.notNumber.runtime();
			return null;
		}
	}
	
}
