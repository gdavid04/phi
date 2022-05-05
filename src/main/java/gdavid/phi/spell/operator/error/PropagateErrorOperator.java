package gdavid.phi.spell.operator.error;

import com.mojang.datafixers.util.Either;

import gdavid.phi.spell.error.PropagatingSpellRuntimeException;
import gdavid.phi.spell.param.ErrorParam;
import gdavid.phi.util.EvalHelper;
import gdavid.phi.util.IModifierFlagProvider;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Any;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PropagateErrorOperator extends PieceOperator implements IModifierFlagProvider {
	
	ErrorParam target;
	
	public PropagateErrorOperator(Spell spell) {
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
		// Any is returned in invalid spells only as Psi doesn't handle it properly as
		// an evaluation type
		if (!isInGrid || !paramSides.get(target).isEnabled()) return Any.class;
		try {
			SpellPiece piece = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(target));
			if (piece == null || EvalHelper.isLoop(piece)) return Any.class;
			return piece.getEvaluationType();
		} catch (SpellCompilationException e) {
			return Any.class;
		}
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Either<Object, PropagatingSpellRuntimeException> val = target.get(this, context);
		if (val.map(l -> false, r -> true)) {
			PropagatingSpellRuntimeException error = val.right().get();
			throw new PropagatingSpellRuntimeException(error.getMessage(), error.x, error.y, true, false);
		}
		return val.left().get();
	}
	
}
