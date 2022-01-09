package gdavid.phi.spell.operator.text;

import gdavid.phi.spell.Errors;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class CharacterFromCodeOperator extends PieceOperator {
	
	SpellParam<Number> code;
	
	public CharacterFromCodeOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(code = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.GREEN, false, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		int c = getParamValue(context, code).intValue();
		if (c < 0 || c >= 256) Errors.runtime(SpellRuntimeException.OUT_OF_BOUNDS);
		return String.valueOf((char) c);
	}
	
}
