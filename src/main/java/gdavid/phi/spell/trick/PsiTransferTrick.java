package gdavid.phi.spell.trick;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.entity.PsiProjectileEntity;
import gdavid.phi.util.ParamHelper;
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

public class PsiTransferTrick extends PieceTrick {
	
	public static final String flag = Phi.modId + ":psi_transfer";
	
	SpellParam<Vector3> direction;
	SpellParam<Number> psi;
	
	public PsiTransferTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
		addParam(psi = new ParamNumber(SpellParam.GENERIC_NAME_POWER, SpellParam.BLUE, false, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		int psiVal = (int) ParamHelper.positive(this, psi);
		meta.addStat(EnumSpellStat.POTENCY, psiVal);
		meta.addStat(EnumSpellStat.COST, psiVal);
		meta.setFlag(flag, true);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 directionVal = ParamHelper.nonNull(this, context, direction).copy().normalize();
		int psiVal = getNonnullParamValue(context, psi).intValue();
		float efficiency = 0.98f;
		int value = (int) (psiVal * efficiency);
		if (value <= 0) return null;
		if (context.focalPoint.getEntityWorld() instanceof ServerWorld) {
			PsiProjectileEntity projectile = new PsiProjectileEntity(context.focalPoint.getEntityWorld(),
					directionVal.toVec3D(), value);
			projectile.setPosition(context.focalPoint.getPosX(),
					context.focalPoint.getPosY() + context.focalPoint.getEyeHeight() - (context.focalPoint instanceof MPUCaster ? 0 : 0.5),
					context.focalPoint.getPosZ());
			projectile.setOrigin();
			projectile.getEntityWorld().addEntity(projectile);
		}
		return null;
	}
	
}
