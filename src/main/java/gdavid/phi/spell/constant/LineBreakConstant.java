package gdavid.phi.spell.constant;

import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.TextParam;
import vazkii.psi.api.spell.*;

public class LineBreakConstant extends SpellPiece {
	
	SpellParam<String> prefix;
	
	public LineBreakConstant(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(prefix = new TextParam(Param.pre.name, SpellParam.GRAY, true, true));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return getParamValueOrDefault(context, prefix, "") + "\n";
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
	@Override
	public EnumPieceType getPieceType() {
		return EnumPieceType.CONSTANT;
	}
	
	@Override
	public Object evaluate() throws SpellCompilationException {
		return getParamEvaluationeOrDefault(prefix, "") + "\n";
	}
	
}
