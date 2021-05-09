package gdavid.phi.spell.trick;

import java.util.Map.Entry;
import java.util.Optional;

import gdavid.phi.spell.ModPieces;
import gdavid.phi.util.ReferenceParam;
import vazkii.psi.api.spell.CompiledSpell.Action;
import vazkii.psi.api.spell.CompiledSpell.CatchHandler;
import vazkii.psi.api.spell.EnumSpellStat;
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

public class EarlyEvaluateTrick extends PieceTrick {
	
	ReferenceParam target;
	SpellParam<Number> condition;
	
	public EarlyEvaluateTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ReferenceParam(SpellParam.GENERIC_NAME_TARGET, SpellParam.RED, false));
		addParam(condition = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.BLUE, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (Math.abs(getNonnullParamValue(context, condition).doubleValue()) >= 1) {
			return null;
		}
		try {
			SpellPiece piece = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(target));
			hoist(piece, context);
		} catch (SpellCompilationException e) {
			throw new SpellRuntimeException(ModPieces.Errors.errored);
		}
		return null;
	}
	
	public void hoist(SpellPiece piece, SpellContext context) throws SpellCompilationException {
		Optional<Action> opt = context.actions.stream().filter(action -> action.piece == piece).findFirst();
		if (!opt.isPresent()) {
			return;
		}
		context.actions.remove(opt.get());
		context.actions.push(opt.get());
		CatchHandler catchHandler = context.cspell.errorHandlers.get(piece);
		if (catchHandler != null) {
			hoist(catchHandler.handlerPiece, context);
		}
		for (Entry<SpellParam<?>, Side> param : piece.paramSides.entrySet()) {
			if (!param.getValue().isEnabled() || param.getKey() instanceof ReferenceParam) {
				continue;
			}
			hoist(spell.grid.getPieceAtSideWithRedirections(piece.x, piece.y, param.getValue()), context);
		}
	}
	
}
