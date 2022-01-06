package gdavid.phi.spell.connector;

import gdavid.phi.util.IWarpRedirector;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
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
		if (paramSides.get(direction) != Side.OFF) side = paramSides.get(direction);
		try {
			Class<?> clazz = Class.forName("vazkii.psi.common.spell.other.PieceConnector");
			SpellPiece connector = (SpellPiece) clazz.getConstructor(Spell.class).newInstance(spell);
			connector.paramSides.put((SpellParam<?>) clazz.getField("target").get(connector), side);
			connector.x = x + side.offx;
			connector.y = y + side.offy;
			return connector;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
