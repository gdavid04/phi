package gdavid.phi.spell.trick;

import gdavid.phi.entity.PsionWaveEntity;
import gdavid.phi.spell.ModPieces;
import gdavid.phi.util.ParamHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.server.ServerWorld;
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

public class PsionWaveTrick extends PieceTrick {
	
	SpellParam<Vector3> direction;
	SpellParam<Number> speed;
	SpellParam<Number> frequency;
	SpellParam<Number> distance;
	
	public PsionWaveTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
		addParam(speed = new ParamNumber(ModPieces.Params.speed, SpellParam.RED, false, true));
		addParam(frequency = new ParamNumber(ModPieces.Params.frequency, SpellParam.BLUE, false, true));
		addParam(distance = new ParamNumber(SpellParam.GENERIC_NAME_DISTANCE, SpellParam.CYAN, false, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		double speedVal = ParamHelper.positive(this, speed);
		if (speedVal < 1) throw new SpellCompilationException(ModPieces.Errors.minWave);
		double frequencyVal = ParamHelper.positive(this, frequency);
		if (frequencyVal < 1) throw new SpellCompilationException(ModPieces.Errors.minWave);
		double distanceVal = ParamHelper.positive(this, distance);
		if (distanceVal < 1) throw new SpellCompilationException(ModPieces.Errors.minWave);
		if (distanceVal > 32) throw new SpellCompilationException(ModPieces.Errors.range);
		meta.addStat(EnumSpellStat.POTENCY, (int) (speedVal * Math.pow(frequencyVal, 1.2) * distanceVal));
		meta.addStat(EnumSpellStat.COST, (int) (speedVal * Math.pow(frequencyVal, 1.2) * distanceVal));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 directionVal = ParamHelper.nonNull(this, context, direction).copy().normalize();
		float speedVal = getNonnullParamValue(context, speed).floatValue();
		float frequencyVal = getNonnullParamValue(context, frequency).floatValue();
		float distanceVal = getNonnullParamValue(context, distance).floatValue();
		if (speedVal < 1 || frequencyVal < 1 || distanceVal < 1) {
			return null;
		}
		distanceVal = Math.max(1, distanceVal);
		if (context.focalPoint.getEntityWorld() instanceof ServerWorld) {
			PsionWaveEntity wave = new PsionWaveEntity(context.focalPoint.getEntityWorld(),
					new Vector3f((float) directionVal.x, (float) directionVal.y, (float) directionVal.z), speedVal,
					frequencyVal, distanceVal);
			wave.setPosition(context.focalPoint.getPosX(),
					context.focalPoint.getPosY() + context.focalPoint.getEyeHeight() - 0.5,
					context.focalPoint.getPosZ());
			wave.setShooter(context.focalPoint);
			wave.getEntityWorld().addEntity(wave);
		}
		context.delay = 5;
		return null;
	}
	
}
