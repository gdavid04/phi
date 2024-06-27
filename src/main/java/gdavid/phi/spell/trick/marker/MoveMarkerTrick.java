package gdavid.phi.spell.trick.marker;

import gdavid.phi.entity.MarkerEntity;
import gdavid.phi.spell.Errors;
import gdavid.phi.util.ParamHelper;
import net.minecraft.world.entity.Entity;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class MoveMarkerTrick extends PieceTrick {
	
	SpellParam<Entity> target;
	SpellParam<Vector3> position;
	
	public MoveMarkerTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamEntity(SpellParam.GENERIC_NAME_TARGET, SpellParam.YELLOW, false, false));
		addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		meta.addStat(EnumSpellStat.POTENCY, 1);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Entity targetVal = getNonnullParamValue(context, target);
		Vector3 positionVal = ParamHelper.nonNull(this, context, position);
		if (!(targetVal instanceof MarkerEntity)
				|| ((MarkerEntity) targetVal).getOwner() != context.caster.getUUID())
			Errors.invalidTarget.runtime();
		if (MathHelper.pointDistanceSpace(targetVal.getX(), targetVal.getY(), targetVal.getZ(),
				context.focalPoint.getX(), context.focalPoint.getY(),
				context.focalPoint.getZ()) > SpellContext.MAX_DISTANCE * 2
				|| MathHelper.pointDistanceSpace(positionVal.x, positionVal.y, positionVal.z,
						context.focalPoint.getX(), context.focalPoint.getY(),
						context.focalPoint.getZ()) > SpellContext.MAX_DISTANCE * 2) {
			Errors.runtime(SpellRuntimeException.OUTSIDE_RADIUS);
		}
		targetVal.setPos(positionVal.x, positionVal.y, positionVal.z);
		return null;
	}
	
}
