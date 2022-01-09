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

public class CharacterCodeAtOperator extends PieceOperator {
	
	SpellParam<String> text;
	SpellParam<Number> position;
	
	public CharacterCodeAtOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(text = new TextParam(Param.text.name, SpellParam.BLUE, false, false));
		addParam(position = new ParamNumber(SpellParam.GENERIC_NAME_POSITION, SpellParam.RED, true, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		String str = getParamValue(context, text);
		int pos = getParamValueOrDefault(context, position, 1).intValue() - 1;
		if (pos < 0 || pos >= str.length()) Errors.runtime(SpellRuntimeException.OUT_OF_BOUNDS);
		return (int) str.charAt(pos);
	}
	
}
