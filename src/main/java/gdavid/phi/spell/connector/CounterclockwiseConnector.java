package gdavid.phi.spell.connector;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellParam.Side;

public class CounterclockwiseConnector extends ClockwiseConnector {
	
	public CounterclockwiseConnector(Spell spell) {
		super(spell);
	}
	
	@Override
	public Side remapSide(Side side) {
		return side.rotateCW();
	}
	
	@Override
	public Side reverseSide(Side side) {
		return side.rotateCCW();
	}
	
}
