package gdavid.phi.spell.operator.text;

import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.TextParam;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceOperator;

public class AppendTextOperator extends PieceOperator {
	
	SpellParam<String> a, b, c, d;
	
	public AppendTextOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(a = new TextParam(Param.text1.name, SpellParam.RED, false, false));
		addParam(b = new TextParam(Param.text2.name, SpellParam.GREEN, false, false));
		addParam(c = new TextParam(Param.text3.name, SpellParam.YELLOW, true, false));
		addParam(d = new TextParam(Param.text4.name, SpellParam.CYAN, true, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return getParamValue(context, a) + getParamValue(context, b) + getParamValueOrDefault(context, c, "")
				+ getParamValueOrDefault(context, d, "");
	}
	
}
