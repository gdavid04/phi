package gdavid.phi.spell.operator.error;

import gdavid.phi.spell.error.PropagatingSpellRuntimeException;
import gdavid.phi.spell.param.ErrorParam;
import gdavid.phi.util.IModifierFlagProvider;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceOperator;

public class ErrorStatusOperator extends PieceOperator implements IModifierFlagProvider {
	
	ErrorParam target;
	
	public ErrorStatusOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ErrorParam(SpellParam.GENERIC_NAME_TARGET, SpellParam.BROWN, false));
	}
	
	@Override
	public void addFlags(SpellMetadata meta) throws SpellCompilationException {
		SpellPiece piece = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(target));
		if (piece == null) return;
		meta.setFlag(PropagatingSpellRuntimeException.suppressFlag(piece), true);
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Number.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return target.get(this, context).map(l -> 0, r -> 1);
	}
	
}
