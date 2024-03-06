package gdavid.phi.spell.trick.mpu;

import gdavid.phi.block.tile.MPUTile;
import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.spell.Errors;
import gdavid.phi.util.ParamHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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

public class SetTimeTrick extends PieceTrick {
	
	SpellParam<Number> num;
	SpellParam<Vector3> target;
	
	public SetTimeTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(num = new ParamNumber(SpellParam.GENERIC_NAME_TIME, SpellParam.RED, false, false));
		addParam(target = new ParamVector(SpellParam.GENERIC_NAME_TARGET, SpellParam.BLUE, true, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
		meta.addStat(EnumSpellStat.POTENCY, 4);
		if (paramSides.get(target).isEnabled()) meta.addStat(EnumSpellStat.POTENCY, 6);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		int time = getNonnullParamValue(context, num).intValue();
		if (paramSides.get(target).isEnabled()) {
			BlockPos pos = ParamHelper.block(this, context, target);
			Level world = context.focalPoint.getCommandSenderWorld();
			if (!world.hasChunkAt(pos) || !world.mayInteract(context.caster, pos)) {
				return null;
			}
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile instanceof MPUTile) {
				((MPUTile) tile).setTime(time);
			}
		} else {
			if (!(context.caster instanceof MPUCaster)) Errors.noMpu.runtime();
			((MPUCaster) context.caster).setTime(time);
		}
		return null;
	}
	
}
