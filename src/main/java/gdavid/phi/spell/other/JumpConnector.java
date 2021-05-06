package gdavid.phi.spell.other;

import vazkii.psi.api.spell.EnumPieceType;
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

public class JumpConnector extends SpellPiece {
	
	SpellParam<Number> targetX, targetY;
	
	public JumpConnector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) {
		meta.addStat(EnumSpellStat.COMPLEXITY, 3);
	}
	
	@Override
	public void initParams() {
		addParam(targetX = new ParamNumber(SpellParam.GENERIC_NAME_X, SpellParam.RED, false, true));
		addParam(targetY = new ParamNumber(SpellParam.GENERIC_NAME_Y, SpellParam.GREEN, false, true));
	}
	
	@Override
	public EnumPieceType getPieceType() {
		return EnumPieceType.CONNECTOR;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		try {
			SpellPiece target = getTarget();
			return target.getEvaluationType();
		} catch (SpellCompilationException e) {
			return null;
		}
	}
	
	@Override
	public Object evaluate() throws SpellCompilationException {
		SpellPiece target = getTarget();
		return target.evaluate();
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		try {
			SpellPiece target = getTarget();
			return context.evaluatedObjects[target.x][target.y];
		} catch (SpellCompilationException e) {
			throw new SpellRuntimeException(SpellCompilationException.INVALID_PARAM);
		}
	}
	
	public SpellPiece getTarget() throws SpellCompilationException {
		int tx = getNonNullParamEvaluation(targetX).intValue() - 1;
		int ty = getNonNullParamEvaluation(targetX).intValue() - 1;
		SpellPiece target = spell.grid.getPieceAtSideWithRedirections(tx, ty - 1, Side.BOTTOM);
		if (target == null || target instanceof JumpConnector) {
			throw new SpellCompilationException(SpellCompilationException.INVALID_PARAM);
		}
		return target;
	}
	
}
