package gdavid.phi.util;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import vazkii.psi.api.spell.CompiledSpell.Action;
import vazkii.psi.api.spell.CompiledSpell.CatchHandler;
import vazkii.psi.api.spell.IErrorCatcher;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;

public class EvalHelper {
	
	public static boolean isLoop(SpellPiece piece) {
		return isLoop(piece, new HashSet<>());
	}
	
	public static boolean isLoop(SpellPiece piece, Set<SpellPiece> visited) {
		if (piece == null) return false;
		if (visited.contains(piece)) return true;
		visited.add(piece);
		for (Entry<SpellParam<?>, Side> param : piece.paramSides.entrySet()) {
			if (param.getKey() instanceof ReferenceParam && !((ReferenceParam) param.getKey()).checkLoop) continue;
			if (!param.getValue().isEnabled()) continue;
			try {
				SpellPiece other = piece.spell.grid.getPieceAtSideWithRedirections(piece.x, piece.y, param.getValue());
				if (isLoop(other, new HashSet<>(visited))) return true;
			} catch (SpellCompilationException e) {
				return true;
			}
		}
		return false;
	}
	
	public static void hoist(SpellPiece piece, SpellContext context) throws SpellCompilationException {
		Optional<Action> opt = context.actions.stream().filter(action -> action.piece == piece).findFirst();
		if (!opt.isPresent()) {
			return;
		}
		context.actions.remove(opt.get());
		context.actions.push(opt.get());
		hoistParams(piece, context);
	}
	
	public static void reevaluate(SpellPiece piece, SpellContext context) throws SpellCompilationException {
		context.actions.push(context.cspell.new Action(piece));
		hoistParams(piece, context);
	}
	
	public static void hoistParams(SpellPiece piece, SpellContext context) throws SpellCompilationException {
		CatchHandler catchHandler = context.cspell.errorHandlers.get(piece);
		if (catchHandler != null) {
			hoist(catchHandler.handlerPiece, context);
		}
		for (Entry<SpellParam<?>, Side> param : piece.paramSides.entrySet()) {
			if (!param.getValue().isEnabled() || param.getKey() instanceof ReferenceParam
					|| (piece instanceof IErrorCatcher && ((IErrorCatcher) piece).catchParam(param.getKey()))) {
				continue;
			}
			hoist(context.cspell.sourceSpell.grid.getPieceAtSideWithRedirections(piece.x, piece.y, param.getValue()),
					context);
		}
	}
	
}
