package gdavid.phi.spell.selector;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

public class SpellNameSelector extends PieceSelector {
	
	public SpellNameSelector(Spell spell) {
		super(spell);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return spell.name;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
}
