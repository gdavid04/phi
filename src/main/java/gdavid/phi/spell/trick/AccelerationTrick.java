package gdavid.phi.spell.trick;

import gdavid.phi.capability.ModCapabilities;
import gdavid.phi.network.AccelerationMessage;
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
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class AccelerationTrick extends PieceTrick {
	
	SpellParam<Entity> target;
	SpellParam<Vector3> direction;
	SpellParam<Number> power, time;
	
	public AccelerationTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamEntity(SpellParam.GENERIC_NAME_TARGET, SpellParam.YELLOW, false, false));
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
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
		meta.addStat(EnumSpellStat.POTENCY, (int) (timeVal * powerVal * 5 + powerVal * 50));
		meta.addStat(EnumSpellStat.COST, (int) (timeVal * powerVal * 75 + powerVal * 100));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Entity targetVal = getNonnullParamValue(context, target);
		context.verifyEntity(targetVal);
		int timeVal = getNonnullParamValue(context, time).intValue();
		double powerVal = getNonnullParamValue(context, power).doubleValue() * 0.3;
		Vector3 accel = ParamHelper.nonNull(this, context, direction).copy().normalize().multiply(powerVal);
		targetVal.getCapability(ModCapabilities.acceleration).ifPresent(cap -> cap.addAcceleration(accel, timeVal));
		if (targetVal instanceof PlayerEntity) {
			Messages.send(new AccelerationMessage(accel, timeVal), (PlayerEntity) targetVal);
		}
		return null;
	}
	
}
