package gdavid.phi.spell.operator.error;

import java.util.function.Function;

import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.spell.error.PropagatingSpellRuntimeException;
import gdavid.phi.spell.param.ErrorParam;
import gdavid.phi.util.EvalHelper;
import gdavid.phi.util.IModifierFlagProvider;
import gdavid.phi.util.TypeHelper;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Any;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;
import vazkii.psi.api.spell.piece.PieceOperator;

public class ErrorCatcherOperator extends PieceOperator implements IModifierFlagProvider {
	
	ErrorParam target;
	SpellParam<Any> fallback;
	
	public ErrorCatcherOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ErrorParam(SpellParam.GENERIC_NAME_TARGET, SpellParam.BROWN, false));
		addParam(fallback = new ParamAny(Param.fallback.name, SpellParam.GRAY, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		SpellPiece piece = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(target));
		if (piece != null && piece.getEvaluationType() != Void.class && !paramSides.get(fallback).isEnabled()) {
			Errors.compile(SpellCompilationException.UNSET_PARAM);
		}
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
		if (!isInGrid || !paramSides.get(target).isEnabled() || !paramSides.get(fallback).isEnabled()) return Any.class;
		try {
			SpellPiece piece1 = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(target));
			if (piece1 == null || EvalHelper.isLoop(piece1)) return Any.class;
			SpellPiece piece2 = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(fallback));
			if (piece2 == null || EvalHelper.isLoop(piece2)) return Void.class;
			if (piece1.getEvaluationType() == Void.class || piece2.getEvaluationType() == Void.class) return Void.class;
			Class<?> clazz = TypeHelper.commonSuper(piece1.getEvaluationType(), piece2.getEvaluationType());
			return clazz == null ? Object.class : clazz;
		} catch (SpellCompilationException e) {
			return Any.class;
		}
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return target.get(this, context).map(Function.identity(), error -> getRawParamValue(context, fallback));
	}
	
}
