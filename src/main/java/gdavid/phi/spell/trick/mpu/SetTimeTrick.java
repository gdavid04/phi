package gdavid.phi.spell.trick.mpu;

import gdavid.phi.block.tile.MPUTile;
import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.spell.ModPieces;
import gdavid.phi.util.ParamHelper;
import net.minecraft.tileentity.TileEntity;
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
			World world = context.focalPoint.getEntityWorld();
			if (!world.isBlockLoaded(pos) || !world.isBlockModifiable(context.caster, pos)) {
				return null;
			}
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof MPUTile) {
				((MPUTile) tile).setTime(time);
			}
		} else {
			if (!(context.caster instanceof MPUCaster)) {
				throw new SpellRuntimeException(ModPieces.Errors.noMpu);
			}
			((MPUCaster) context.caster).setTime(time);
		}
		return null;
	}
	
}
