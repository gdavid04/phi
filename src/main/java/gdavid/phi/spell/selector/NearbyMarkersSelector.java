package gdavid.phi.spell.selector;

import gdavid.phi.entity.MarkerEntity;
import gdavid.phi.spell.Errors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceSelector;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class NearbyMarkersSelector extends PieceSelector {
	
	SpellParam<Vector3> position;
	SpellParam<Number> radius;
	
	public NearbyMarkersSelector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, true, false));
		addParam(radius = new ParamNumber(SpellParam.GENERIC_NAME_RADIUS, SpellParam.GREEN, true, false));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 positionVal = getParamValueOrDefault(context, position,
				Vector3.fromVec3d(context.focalPoint.position()));
		double radiusVal = getParamValueOrDefault(context, radius, SpellContext.MAX_DISTANCE * 2).doubleValue();
		if (radiusVal <= 0) Errors.runtime(SpellRuntimeException.NON_POSITIVE_VALUE);
		if (MathHelper.pointDistanceSpace(positionVal.x, positionVal.y, positionVal.z, context.focalPoint.getX(),
				context.focalPoint.getY(), context.focalPoint.getZ()) > SpellContext.MAX_DISTANCE * 2) {
			Errors.runtime(SpellRuntimeException.OUTSIDE_RADIUS);
		}
		AABB boundingBox = AABB.ofSize(positionVal.toVec3D(), radiusVal, radiusVal, radiusVal);
		boundingBox = boundingBox
				.intersect(AABB.ofSize(context.focalPoint.position(),
						SpellContext.MAX_DISTANCE * 2, SpellContext.MAX_DISTANCE * 2, SpellContext.MAX_DISTANCE * 2));
		return EntityListWrapper
				.make(context.focalPoint.getCommandSenderWorld().getEntitiesOfClass(Entity.class, boundingBox, e -> e instanceof MarkerEntity));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return EntityListWrapper.class;
	}
	
}
