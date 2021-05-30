package gdavid.phi.spell.operator;

import gdavid.phi.spell.ModPieces;
import gdavid.phi.util.ParamHelper;
import gdavid.phi.util.TypeHelper;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Any;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class BranchOperator extends PieceOperator {
	
	SpellParam<Any> positive, negative;
	SpellParam<Number> condition;
	
	public BranchOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(positive = new ParamAny(ModPieces.Params.positive, SpellParam.RED, false));
		addParam(negative = new ParamAny(ModPieces.Params.negative, SpellParam.GREEN, false));
		addParam(condition = new ParamNumber(ModPieces.Params.condition, SpellParam.BLUE, false, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		if (!isInGrid) return Any.class;
		try {
			SpellPiece piece1 = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(positive));
			if (piece1 == null || ParamHelper.isLoop(piece1)) return Object.class;
			SpellPiece piece2 = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(negative));
			if (piece2 == null || ParamHelper.isLoop(piece2)) return Object.class;
			Class<?> clazz = TypeHelper.commonSuper(piece1.getEvaluationType(), piece2.getEvaluationType());
			return clazz == null ? Object.class : clazz;
		} catch (SpellCompilationException e) {
			return Object.class;
		}
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Double cond = getParamValue(context, condition).doubleValue();
		if (cond > 0) {
			return getRawParamValue(context, positive);
		} else if (cond < 0) {
			return getRawParamValue(context, negative);
		} else {
			Object a = getRawParamValue(context, positive);
			Object b = getRawParamValue(context, negative);
			if (!a.equals(b)) throw new SpellRuntimeException(ModPieces.Errors.ambiguous);
			return a;
		}
	}
	
}
