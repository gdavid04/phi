package gdavid.phi.spell.trick;

import gdavid.phi.block.ModBlocks;
import gdavid.phi.util.ParamHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

public class ShadowTrick extends PieceTrick {
	
	SpellParam<Vector3> position;
	SpellParam<Number> time;
	// TODO light value
	
	public ShadowTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
		addParam(time = new ParamNumber(SpellParam.GENERIC_NAME_TIME, SpellParam.PURPLE, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		int timeVal = (int) ParamHelper.positive(this, time);
		meta.addStat(EnumSpellStat.POTENCY, timeVal / 2);
		meta.addStat(EnumSpellStat.COST, 40);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public Object execute(SpellContext context) throws SpellRuntimeException {
		BlockPos pos = ParamHelper.block(this, context, position);
		int timeVal = getNonnullParamValue(context, time).intValue();
		World world = context.focalPoint.getEntityWorld();
		if (!world.isBlockLoaded(pos) || !world.isBlockModifiable(context.caster, pos)) {
			return null;
		}
		BlockState block = world.getBlockState(pos);
		if (block.isAir(world, pos) || block.getMaterial().isReplaceable()) {
			if (world.setBlockState(pos, ModBlocks.shadow.getDefaultState())) {
				world.getPendingBlockTicks().scheduleTick(pos, ModBlocks.shadow, timeVal);
			}
		}
		return null;
	}
	
}
