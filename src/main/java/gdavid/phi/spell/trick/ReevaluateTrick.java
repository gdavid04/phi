package gdavid.phi.spell.trick;

import gdavid.phi.spell.ModPieces;
import gdavid.phi.util.ParamHelper;
import gdavid.phi.util.ReferenceParam;
import java.util.Map.Entry;
import vazkii.psi.api.spell.CompiledSpell.CatchHandler;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.IErrorCatcher;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;
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
		if (paramSides.get(condition).isEnabled()) meta.addStat(EnumSpellStat.COMPLEXITY, 1);
		if (ParamHelper.isLoop(this)) throw new SpellCompilationException(SpellCompilationException.INFINITE_LOOP);
		SpellPiece piece = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(target));
		if (piece == null || !paramSides.get(target).isEnabled()) throw new SpellCompilationException(SpellCompilationException.INVALID_PARAM);
		piece.addToMetadata(meta);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (Math.abs(getParamValueOrDefault(context, condition, 0).doubleValue()) >= 1) return null;
		try {
			SpellPiece piece = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(target));
			reevaluate(piece, context);
		} catch (SpellCompilationException e) {
			throw new SpellRuntimeException(ModPieces.Errors.errored); // NOPMD
		}
		return null;
	}
	
	public static void reevaluate(SpellPiece piece, SpellContext context) throws SpellCompilationException {
		context.actions.push(context.cspell.new Action(piece));
		CatchHandler catchHandler = context.cspell.errorHandlers.get(piece);
		if (catchHandler != null) {
			EarlyEvaluateTrick.hoist(catchHandler.handlerPiece, context);
		}
		for (Entry<SpellParam<?>, Side> param : piece.paramSides.entrySet()) {
			if (!param.getValue().isEnabled() || param.getKey() instanceof ReferenceParam
					|| (piece instanceof IErrorCatcher && ((IErrorCatcher) piece).catchParam(param.getKey()))) {
				continue;
			}
			EarlyEvaluateTrick.hoist(context.cspell.sourceSpell.grid.getPieceAtSideWithRedirections(piece.x, piece.y, param.getValue()), context);
		}
	}
	
}
