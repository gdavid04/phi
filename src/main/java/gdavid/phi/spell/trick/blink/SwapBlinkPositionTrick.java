package gdavid.phi.spell.trick.blink;

import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.util.ParamHelper;
import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.phys.Vec3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class SwapBlinkPositionTrick extends PieceTrick {
	
	SpellParam<Entity> a, b;
	SpellParam<Number> distance;
	
	public SwapBlinkPositionTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(a = new ParamEntity(Param.target1.name, SpellParam.YELLOW, false, false));
		addParam(b = new ParamEntity(Param.target2.name, SpellParam.YELLOW, true, false));
		addParam(distance = new ParamNumber(SpellParam.GENERIC_NAME_DISTANCE, SpellParam.RED, false, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		double maxDistance = ParamHelper.positive(this, distance);
		meta.addStat(EnumSpellStat.POTENCY, (int) (Math.sqrt(2 * maxDistance) * 40));
		meta.addStat(EnumSpellStat.COST, (int) (maxDistance * 40));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		double distanceVal = getNonnullParamValue(context, distance).doubleValue();
		Entity e1 = getNonnullParamValue(context, a);
		Entity e2 = getParamValueOrDefault(context, b, context.caster);
		context.verifyEntity(e1);
		context.verifyEntity(e2);
		if (!context.isInRadius(e1) || !context.isInRadius(e2)) {
			Errors.runtime(SpellRuntimeException.OUTSIDE_RADIUS);
		}
		Vec3 offset = e2.position().subtract(e1.position());
		if (offset.lengthSqr() > distanceVal * distanceVal) return null;
		Vec3 pos1 = e1.position();
		e1.setPos(e2.getX(), e2.getY(), e2.getZ());
		if (e1 instanceof ServerPlayer) {
			ServerGamePacketListenerImpl c = ((ServerPlayer) e1).connection;
			c.teleport(e2.getX(), e2.getY(), e2.getZ(), e1.getYRot(), e1.getXRot(),
					EnumSet.of(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT, ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT));
			c.resetPosition();
		}
		e2.setPos(pos1.x, pos1.y, pos1.z);
		if (e2 instanceof ServerPlayer) {
			ServerGamePacketListenerImpl c = ((ServerPlayer) e2).connection;
			c.teleport(pos1.x, pos1.y, pos1.z, e2.getYRot(), e2.getXRot(),
					EnumSet.of(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT, ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT));
			c.resetPosition();
		}
		return null;
	}
	
}
