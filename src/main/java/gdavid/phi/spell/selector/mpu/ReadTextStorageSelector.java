package gdavid.phi.spell.selector.mpu;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.block.tile.TextSUTile;
import gdavid.phi.spell.Errors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceSelector;

import java.util.List;

public class ReadTextStorageSelector extends PieceSelector {
	
	SpellParam<Vector3> direction;
	
	public ReadTextStorageSelector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		meta.addStat(EnumSpellStat.POTENCY, 10);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addToTooltipAfterShift(List<Component> tooltip) {
		tooltip.add(Component.translatable("phi.tooltip.require_mpu"));
		super.addToTooltipAfterShift(tooltip);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 dir = getNonnullParamValue(context, direction);
		Direction d = Direction.getNearest(dir.x, dir.y, dir.z);
		if (!(context.caster instanceof MPUCaster)) Errors.noMpu.runtime();
		BlockPos pos = ((MPUCaster) context.caster).getConnected(d);
		if (pos == null) Errors.runtime(SpellRuntimeException.NULL_TARGET);
		BlockEntity tile = context.caster.level().getBlockEntity(pos);
		if (!(tile instanceof TextSUTile)) Errors.runtime(SpellRuntimeException.NULL_TARGET);
		return ((TextSUTile) tile).getText();
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
}
