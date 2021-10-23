package gdavid.phi.spell.operator.vector;

import gdavid.phi.util.ParamHelper;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PreciseRaycastOperator extends PieceOperator {
	
	SpellParam<Vector3> origin, ray;
	SpellParam<Number> max;
	
	public PreciseRaycastOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(origin = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
		addParam(ray = new ParamVector(SpellParam.GENERIC_NAME_RAY, SpellParam.GREEN, false, false));
		addParam(max = new ParamNumber(SpellParam.GENERIC_NAME_MAX, SpellParam.PURPLE, true, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Vector3.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 start = getNonnullParamValue(context, origin);
		Vector3 direction = ParamHelper.nonNull(this, context, ray);
		double length = getParamValueOrDefault(context, max, SpellContext.MAX_DISTANCE).doubleValue();
		length = Math.max(Math.min(length, SpellContext.MAX_DISTANCE), -SpellContext.MAX_DISTANCE);
		Vector3 end = start.copy().add(direction.copy().normalize().multiply(length));
		BlockRayTraceResult res = context.focalPoint.world.rayTraceBlocks(
				new RayTraceContext(start.toVec3D(), end.toVec3D(), BlockMode.OUTLINE, FluidMode.NONE, context.focalPoint));
		if (res.getType() == RayTraceResult.Type.MISS) {
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		}
		return Vector3.fromVec3d(res.getHitVec());
	}
	
}
