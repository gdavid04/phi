package gdavid.phi.spell.operator.vector.raycast;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.spell.Errors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
		Vec3 start = source.position().add(0, source.getEyeHeight(), 0);
		if (source instanceof MPUCaster) start = start.add(source.getLookAngle());
		Vec3 end = start.add(source.getLookAngle().scale(distance));
		BlockHitResult res = context.focalPoint.level
				.clip(new ClipContext(start, end, Block.OUTLINE, Fluid.NONE, context.focalPoint));
		if (res.getType() == HitResult.Type.MISS) Errors.runtime(SpellRuntimeException.NULL_VECTOR);
		return Vector3.fromDirection(res.getDirection());
	}
	
}
