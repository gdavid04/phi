package gdavid.phi.spell.trick.blink;

import java.util.EnumSet;
import java.util.Stack;

import gdavid.phi.spell.Errors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.core.handler.PlayerDataHandler;

public class BlinkEidosReversalTrick extends PieceTrick {
	
	SpellParam<Number> time;
	
	public BlinkEidosReversalTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(time = new ParamNumber(SpellParam.GENERIC_NAME_TIME, SpellParam.PURPLE, false, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		Double timeVal = getNonNullParamEvaluation(time).doubleValue();
		if (timeVal <= 0 || timeVal != timeVal.intValue()) {
			Errors.compile(SpellCompilationException.NON_POSITIVE_INTEGER);
		}
		meta.addStat(EnumSpellStat.POTENCY, (int) (timeVal * 20 + 30));
		meta.addStat(EnumSpellStat.COST, (int) (timeVal * 40));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		int timeVal = getNonnullParamValue(context, time).intValue() * 20;
		if (timeVal == 0) return null;
		Stack<Vector3> eidosLog = PlayerDataHandler.get(context.caster).eidosChangelog;
		if (eidosLog.size() == 0) return null;
		eidosLog.subList(Math.max(eidosLog.size() - timeVal + 1, 1), eidosLog.size()).clear();
		Vector3 pos = eidosLog.pop();
		context.caster.setPosition(pos.x, pos.y, pos.z);
		if (context.caster instanceof ServerPlayerEntity) {
			ServerPlayNetHandler c = ((ServerPlayerEntity) context.caster).connection;
			c.setPlayerLocation(pos.x, pos.y, pos.z, context.caster.rotationYaw, context.caster.rotationPitch,
					EnumSet.of(SPlayerPositionLookPacket.Flags.X_ROT, SPlayerPositionLookPacket.Flags.Y_ROT));
			c.captureCurrentPosition();
		}
		return null;
	}
	
}
