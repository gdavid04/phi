package gdavid.phi.spell.trick.marker;

import gdavid.phi.entity.MarkerEntity;
import gdavid.phi.entity.SpiritEntity;
import gdavid.phi.spell.Errors;
import gdavid.phi.util.ParamHelper;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class ConjureMarkerTrick extends PieceTrick {
	
	SpellParam<Vector3> position;
	SpellParam<Number> time;
	
	public ConjureMarkerTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
		addParam(time = new ParamNumber(SpellParam.GENERIC_NAME_TIME, SpellParam.PURPLE, false, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		Double timeVal = getNonNullParamEvaluation(time).doubleValue();
		if (timeVal <= 0 || timeVal != timeVal.intValue()) {
			Errors.compile(SpellCompilationException.NON_POSITIVE_INTEGER);
		}
		meta.addStat(EnumSpellStat.POTENCY, (int) Math.max(1, timeVal / 5));
		meta.addStat(EnumSpellStat.COST, (int) Math.max(1, timeVal / 20));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 positionVal = ParamHelper.nonNull(this, context, position);
		int timeVal = getNonnullParamValue(context, time).intValue();
		if (timeVal < 1) Errors.runtime(SpellRuntimeException.NON_POSITIVE_VALUE);
		if (MathHelper.pointDistanceSpace(positionVal.x, positionVal.y, positionVal.z, context.focalPoint.getPosX(),
				context.focalPoint.getPosY(), context.focalPoint.getPosZ()) > SpellContext.MAX_DISTANCE * 2) {
			Errors.runtime(SpellRuntimeException.OUTSIDE_RADIUS);
		}
		World world = context.focalPoint.getEntityWorld();
		MarkerEntity marker = new MarkerEntity(world, context.caster, timeVal);
		marker.setPosition(positionVal.x, positionVal.y, positionVal.z);
		world.addEntity(marker);
		return marker;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Entity.class;
	}
	
}
