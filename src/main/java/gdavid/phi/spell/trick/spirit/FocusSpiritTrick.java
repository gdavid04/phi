package gdavid.phi.spell.trick.spirit;

import gdavid.phi.Phi;
import gdavid.phi.entity.SpiritEntity;
import gdavid.phi.spell.Errors;
import net.minecraft.world.entity.Entity;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceTrick;

public class FocusSpiritTrick extends PieceTrick {
	
	public static final String originalFocus = Phi.modId + ":original_focus";
	
	SpellParam<Entity> target;
	
	public FocusSpiritTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamEntity(SpellParam.GENERIC_NAME_TARGET, SpellParam.YELLOW, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
		meta.addStat(EnumSpellStat.POTENCY, 50);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Entity targetVal = getNonnullParamValue(context, target);
		if (!(targetVal instanceof SpiritEntity)
				|| ((SpiritEntity) targetVal).getOwner() != context.caster.getUUID())
			Errors.invalidTarget.runtime();
		context.customData.computeIfAbsent(originalFocus, k -> context.focalPoint);
		context.focalPoint = targetVal;
		return context.focalPoint;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Entity.class;
	}
	
}
