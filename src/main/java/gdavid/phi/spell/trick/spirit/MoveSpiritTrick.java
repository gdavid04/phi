package gdavid.phi.spell.trick.spirit;

import gdavid.phi.entity.SpiritEntity;
import gdavid.phi.spell.Errors;
import gdavid.phi.util.ParamHelper;
import net.minecraft.entity.Entity;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class MoveSpiritTrick extends PieceTrick {
	
	SpellParam<Entity> target;
	SpellParam<Vector3> direction;
	SpellParam<Number> distance;
	
	public MoveSpiritTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamEntity(SpellParam.GENERIC_NAME_TARGET, SpellParam.YELLOW, true, false));
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.BLUE, false, false));
		addParam(distance = new ParamNumber(SpellParam.GENERIC_NAME_DISTANCE, SpellParam.RED, false, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		double dist = ParamHelper.positive(this, distance);
		meta.addStat(EnumSpellStat.POTENCY, (int) (dist * 50));
		meta.addStat(EnumSpellStat.COST, (int) (dist * 50));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Entity targetVal = context.focalPoint;
		if (paramSides.get(target).isEnabled()) targetVal = getNonnullParamValue(context, target);
		Vector3 position = ParamHelper.nonNull(this, context, direction).copy().normalize()
				.multiply(getNonnullParamValue(context, distance).doubleValue()).add(Vector3.fromEntity(targetVal));
		if (!(targetVal instanceof SpiritEntity)
				|| ((SpiritEntity) targetVal).getOwner() != context.caster.getUniqueID())
			Errors.invalidTarget.runtime();
		targetVal.setPosition(position.x, position.y, position.z);
		return null;
	}
	
}
