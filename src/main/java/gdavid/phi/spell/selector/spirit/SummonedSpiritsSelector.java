package gdavid.phi.spell.selector.spirit;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class SummonedSpiritsSelector extends PieceSelector {
	
	public SummonedSpiritsSelector(Spell spell) {
		super(spell);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		// TODO
		return EntityListWrapper.EMPTY;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return EntityListWrapper.class;
	}
	
}
