package gdavid.phi.spell.operator.vector.raycast;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.vector.Vector3d;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceOperator;

public class FocusedBlockFaceOperator extends PieceOperator {
	
	SpellParam<Entity> target;
	
	public FocusedBlockFaceOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamEntity(SpellParam.GENERIC_NAME_TARGET, SpellParam.YELLOW, true, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Vector3.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Entity source = getParamValueOrDefault(context, target, context.caster);
		double distance = 32;
		Vector3d start = source.getPositionVec().add(0, source.getEyeHeight(), 0);
		Vector3d end = start.add(source.getLookVec().scale(distance));
		BlockRayTraceResult res = context.focalPoint.world.rayTraceBlocks(new RayTraceContext(start, end,
				BlockMode.OUTLINE, FluidMode.NONE, context.focalPoint));
		if (res.getType() == RayTraceResult.Type.MISS) {
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		}
		return Vector3.fromDirection(res.getFace());
	}
	
}
