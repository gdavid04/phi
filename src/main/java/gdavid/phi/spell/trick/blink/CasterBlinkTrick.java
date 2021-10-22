package gdavid.phi.spell.trick.blink;

import gdavid.phi.util.ParamHelper;
import net.minecraft.entity.player.PlayerEntity;
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

public class CasterBlinkTrick extends PieceTrick {
	
	SpellParam<Vector3> direction;
	SpellParam<Number> distance;
	
	public CasterBlinkTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
		addParam(distance = new ParamNumber(SpellParam.GENERIC_NAME_DISTANCE, SpellParam.RED, false, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		double maxDistance = ParamHelper.positive(this, distance);
		meta.addStat(EnumSpellStat.POTENCY, (int) (maxDistance * 30));
		meta.addStat(EnumSpellStat.COST, (int) (maxDistance * 40));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		double distanceVal = getNonnullParamValue(context, distance).doubleValue();
		Vector3 directionVal = ParamHelper.nonNull(this, context, direction).copy().normalize().multiply(distanceVal);
		context.caster.setPosition(context.caster.getPosX() + directionVal.x, context.caster.getPosY() + directionVal.y,
				context.caster.getPosZ() + directionVal.z);
		try {
			Object message = Class.forName("vazkii.psi.common.network.message.MessageBlink")
					.getConstructor(double.class, double.class, double.class)
					.newInstance(directionVal.x, directionVal.y, directionVal.z);
			Class.forName("vazkii.psi.common.network.MessageRegister")
					.getMethod("sendToPlayer", Object.class, PlayerEntity.class).invoke(null, message, context.caster);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
