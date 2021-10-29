package gdavid.phi.spell.trick;

import gdavid.phi.block.ModBlocks;
import gdavid.phi.util.ParamHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
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

public class ShadowSequenceTrick extends PieceTrick {
	
	SpellParam<Vector3> position;
	SpellParam<Number> time;
	SpellParam<Vector3> target;
	SpellParam<Number> maxBlocks;
	
	public ShadowSequenceTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
		addParam(time = new ParamNumber(SpellParam.GENERIC_NAME_TIME, SpellParam.PURPLE, false, false));
		addParam(target = new ParamVector(SpellParam.GENERIC_NAME_TARGET, SpellParam.GREEN, false, false));
		addParam(maxBlocks = new ParamNumber(SpellParam.GENERIC_NAME_MAX, SpellParam.RED, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		int timeVal = (int) ParamHelper.positive(this, time);
		int max = (int) ParamHelper.positive(this, maxBlocks);
		meta.addStat(EnumSpellStat.POTENCY, timeVal / 2 * max);
		meta.addStat(EnumSpellStat.COST, 40 + 20 * (max - 1));
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 pos = ParamHelper.nonNull(this, context, position);
		int timeVal = getNonnullParamValue(context, time).intValue();
		int max = getNonnullParamValue(context, maxBlocks).intValue();
		Vector3 to = ParamHelper.nonNull(this, context, target).copy().normalize().multiply(max);
		World world = context.focalPoint.getEntityWorld();
		for (BlockPos at : MathHelper.getBlocksAlongRay(pos.toVec3D(), pos.copy().add(to).toVec3D(), max)) {
			if (!context.isInRadius(Vector3.fromBlockPos(at))) {
				throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
			}
			if (!world.isBlockLoaded(at) || !world.isBlockModifiable(context.caster, at)) {
				continue;
			}
			BlockState block = world.getBlockState(at);
			if (block.isAir(world, at) || block.getMaterial().isReplaceable()) {
				if (world.setBlockState(at, ModBlocks.shadow.getDefaultState())) {
					world.getPendingBlockTicks().scheduleTick(at, ModBlocks.shadow, timeVal);
				}
			}
		}
		return null;
	}
	
}
