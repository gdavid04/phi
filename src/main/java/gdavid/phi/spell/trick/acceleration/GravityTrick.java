package gdavid.phi.spell.trick.acceleration;

import gdavid.phi.capability.ModCapabilities;
import gdavid.phi.network.GravityMessage;
import gdavid.phi.network.Messages;
import gdavid.phi.spell.Errors;
import gdavid.phi.util.ParamHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntityListWrapper;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class GravityTrick extends PieceTrick {
	
	SpellParam<EntityListWrapper> target;
	SpellParam<Vector3> center;
	SpellParam<Number> power, time;
	
	public GravityTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamEntityListWrapper(SpellParam.GENERIC_NAME_TARGET, SpellParam.YELLOW, false, false));
		addParam(center = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.GREEN, false, false));
		addParam(power = new ParamNumber(SpellParam.GENERIC_NAME_POWER, SpellParam.RED, false, true));
		addParam(time = new ParamNumber(SpellParam.GENERIC_NAME_TIME, SpellParam.PURPLE, false, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		double powerVal = ParamHelper.positiveOrZero(this, power);
		Double timeVal = getNonNullParamEvaluation(time).doubleValue();
		if (timeVal <= 0 || timeVal != timeVal.doubleValue()) {
			Errors.compile(SpellCompilationException.NON_POSITIVE_INTEGER);
		}
		meta.addStat(EnumSpellStat.POTENCY, (int) (timeVal * powerVal * 10 + powerVal * 100));
		meta.addStat(EnumSpellStat.COST, (int) (timeVal * powerVal * 83 + powerVal * 111));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		EntityListWrapper targetVal = getNonnullParamValue(context, target);
		int timeVal = getNonnullParamValue(context, time).intValue();
		double powerVal = getNonnullParamValue(context, power).doubleValue() * 0.3;
		Vector3 centerVal = getNonnullParamValue(context, center).copy();
		for (Entity e : targetVal) {
			context.verifyEntity(e);
			if (!context.isInRadius(e)) {
				Errors.runtime(SpellRuntimeException.OUTSIDE_RADIUS);
			}
			e.getCapability(ModCapabilities.acceleration).ifPresent(cap -> cap.addGravity(centerVal, powerVal, timeVal));
			if (e instanceof PlayerEntity) {
				Messages.send(new GravityMessage(centerVal, powerVal, timeVal), (PlayerEntity) e);
			}
		}
		return null;
	}
	
}
