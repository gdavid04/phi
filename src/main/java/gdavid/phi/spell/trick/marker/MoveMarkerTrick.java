package gdavid.phi.spell.trick.marker;

import gdavid.phi.entity.MarkerEntity;
import gdavid.phi.spell.ModPieces;
import gdavid.phi.util.ParamHelper;
import net.minecraft.entity.Entity;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class MoveMarkerTrick extends PieceTrick {
	
	SpellParam<Entity> target;
	SpellParam<Vector3> position;
	
	public MoveMarkerTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamEntity(SpellParam.GENERIC_NAME_TARGET, SpellParam.YELLOW, false, false));
		addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		meta.addStat(EnumSpellStat.POTENCY, 1);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Entity targetVal = getNonnullParamValue(context, target);
		Vector3 positionVal = ParamHelper.nonNull(this, context, position);
		if (!(targetVal instanceof MarkerEntity)
				|| ((MarkerEntity) targetVal).getOwner() != context.caster.getUniqueID()) {
			throw new SpellRuntimeException(ModPieces.Errors.invalidTarget);
		}
		if (MathHelper.pointDistanceSpace(targetVal.getPosX(), targetVal.getPosY(), targetVal.getPosZ(),
				context.focalPoint.getPosX(), context.focalPoint.getPosY(),
				context.focalPoint.getPosZ()) > SpellContext.MAX_DISTANCE * 2
				|| MathHelper.pointDistanceSpace(positionVal.x, positionVal.y, positionVal.z,
						context.focalPoint.getPosX(), context.focalPoint.getPosY(),
						context.focalPoint.getPosZ()) > SpellContext.MAX_DISTANCE * 2) {
			throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
		}
		targetVal.setPosition(positionVal.x, positionVal.y, positionVal.z);
		return null;
	}
	
}
