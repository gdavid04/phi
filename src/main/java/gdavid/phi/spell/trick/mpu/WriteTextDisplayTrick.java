package gdavid.phi.spell.trick.mpu;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.block.tile.TextDisplayTile;
import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.TextParam;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

import java.util.List;

public class WriteTextDisplayTrick extends PieceTrick {
	
	SpellParam<Vector3> direction;
	SpellParam<String> text;
	SpellParam<Number> line;
	
	public WriteTextDisplayTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
		addParam(text = new TextParam(Param.text.name, SpellParam.RED, false, false));
		addParam(line = new ParamNumber(Param.line.name, SpellParam.BLUE, true, false));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addToTooltipAfterShift(List<Component> tooltip) {
		tooltip.add(Component.translatable("phi.tooltip.require_mpu"));
		super.addToTooltipAfterShift(tooltip);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
		meta.addStat(EnumSpellStat.POTENCY, 5);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 dir = getNonnullParamValue(context, direction);
		Direction d = Direction.getNearest(dir.x, dir.y, dir.z);
		String str = getNonnullParamValue(context, text);
		if (!(context.caster instanceof MPUCaster)) Errors.noMpu.runtime();
		BlockPos pos = ((MPUCaster) context.caster).getConnected(d);
		if (pos == null) Errors.runtime(SpellRuntimeException.NULL_TARGET);
		BlockEntity tile = context.caster.level().getBlockEntity(pos);
		if (!(tile instanceof TextDisplayTile)) Errors.runtime(SpellRuntimeException.NULL_TARGET);
		if (paramSides.get(line).isEnabled()) {
			((TextDisplayTile) tile).setLine(str, getNonnullParamValue(context, line).intValue());
		} else {
			((TextDisplayTile) tile).appendLine(str);
		}
		return null;
	}
	
}
