package gdavid.phi.spell.other;

import gdavid.phi.util.IWarpRedirector;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellGrid;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Any;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;

public class BridgeConnector extends SpellPiece implements IWarpRedirector {
	
	ParamAny direction;
	
	public BridgeConnector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamAny(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GRAY, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
	}
	
	@Override
	public boolean isInputSide(SpellParam.Side side) {
		return false;
	}
	
	@Override
	public SpellPiece redirect(Side side) {
		Side dir = side;
		if (paramSides.get(direction) != Side.OFF) dir = paramSides.get(direction);
		int tx = x + 2 * dir.offx, ty = y + 2 * dir.offy;
		if (tx < 0 || tx >= SpellGrid.GRID_SIZE || ty < 0 || ty >= SpellGrid.GRID_SIZE) return null;
		return spell.grid.gridData[tx][ty];
	}
	
	@Override
	public String getSortingName() {
		return "00000000000";
	}
	
	@Override
	public EnumPieceType getPieceType() {
		return EnumPieceType.CONNECTOR;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Any.class;
	}
	
	@Override
	public Object evaluate() throws SpellCompilationException {
		return null;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return null;
	}
	
}
