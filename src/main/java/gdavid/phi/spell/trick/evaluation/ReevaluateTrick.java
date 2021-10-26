package gdavid.phi.spell.trick.evaluation;

import gdavid.phi.spell.ModPieces;
import gdavid.phi.util.EvalHelper;
import gdavid.phi.util.ReferenceParam;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class ReevaluateTrick extends PieceTrick {
	
	ReferenceParam target;
	SpellParam<Number> condition;
	
	public ReevaluateTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ReferenceParam(SpellParam.GENERIC_NAME_TARGET, SpellParam.RED, false).preventLoop());
		addParam(condition = new ParamNumber(ModPieces.Params.condition, SpellParam.BLUE, true, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
		if (EvalHelper.isLoop(this)) throw new SpellCompilationException(SpellCompilationException.INFINITE_LOOP);
		SpellPiece piece = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(target));
		if (piece == null || !paramSides.get(target).isEnabled())
			throw new SpellCompilationException(SpellCompilationException.INVALID_PARAM);
		piece.addToMetadata(meta);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (Math.abs(getParamValueOrDefault(context, condition, 0).doubleValue()) >= 1) return null;
		try {
			SpellPiece piece = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(target));
			EvalHelper.reevaluate(piece, context);
		} catch (SpellCompilationException e) {
			throw new SpellRuntimeException(ModPieces.Errors.errored); // NOPMD
		}
		return null;
	}
	
}
