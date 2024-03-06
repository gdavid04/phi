package gdavid.phi.spell.trick.spirit;

import gdavid.phi.spell.Errors;
import net.minecraft.world.entity.Entity;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceTrick;

public class DefocusSpiritTrick extends PieceTrick {
	
	public DefocusSpiritTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (!context.customData.containsKey(FocusSpiritTrick.originalFocus))
			Errors.runtime(SpellRuntimeException.NULL_TARGET);
		context.focalPoint = (Entity) context.customData.remove(FocusSpiritTrick.originalFocus);
		return context.focalPoint;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Entity.class;
	}
	
}
