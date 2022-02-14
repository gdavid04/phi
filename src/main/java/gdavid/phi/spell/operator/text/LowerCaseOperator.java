package gdavid.phi.spell.operator.text;

import gdavid.phi.api.param.TextParam;
import gdavid.phi.spell.Param;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceOperator;

public class LowerCaseOperator extends PieceOperator {
	
	SpellParam<String> text;
	
	public LowerCaseOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(text = new TextParam(Param.text.name, SpellParam.BLUE, false, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return getParamValue(context, text).toLowerCase();
	}
	
}
