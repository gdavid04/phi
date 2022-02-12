package gdavid.phi.spell.trick.blink;

import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.util.ParamHelper;
import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.math.vector.Vector3d;
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
		Vector3d offset = e2.getPositionVec().subtract(e1.getPositionVec());
		if (offset.lengthSquared() > distanceVal * distanceVal) return null;
		Vector3d pos1 = e1.getPositionVec();
		e1.setPosition(e2.getPosX(), e2.getPosY(), e2.getPosZ());
		if (e1 instanceof ServerPlayerEntity) {
			ServerPlayNetHandler c = ((ServerPlayerEntity) e1).connection;
			c.setPlayerLocation(e2.getPosX(), e2.getPosY(), e2.getPosZ(), e1.rotationYaw, e1.rotationPitch,
					EnumSet.of(SPlayerPositionLookPacket.Flags.X_ROT, SPlayerPositionLookPacket.Flags.Y_ROT));
			c.captureCurrentPosition();
		}
		e2.setPosition(pos1.x, pos1.y, pos1.z);
		if (e2 instanceof ServerPlayerEntity) {
			ServerPlayNetHandler c = ((ServerPlayerEntity) e2).connection;
			c.setPlayerLocation(pos1.x, pos1.y, pos1.z, e2.rotationYaw, e2.rotationPitch,
					EnumSet.of(SPlayerPositionLookPacket.Flags.X_ROT, SPlayerPositionLookPacket.Flags.Y_ROT));
			c.captureCurrentPosition();
		}
		return null;
	}
	
}
