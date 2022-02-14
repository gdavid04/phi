package gdavid.phi.spell.form;

import gdavid.phi.Phi;
import gdavid.phi.api.util.ContextHelper;
import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.spell.Errors;
import gdavid.phi.util.ParamHelper;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;
import vazkii.psi.common.entity.EntitySpellCircle;

public class CircleForm extends PieceTrick {
	
	public static final String loopingFlag = Phi.modId + ":looping";
	
	// SpellParam<Vector3> position;
	
	public CircleForm(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		// addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		if (meta.getFlag(loopingFlag)) Errors.nestedLoop.compile();
		meta.setFlag(loopingFlag, true);
		meta.compoundStatMultiplier(EnumSpellStat.COST, 15);
		meta.setStat(EnumSpellStat.COST, meta.getStat(EnumSpellStat.COST));
		meta.setStatMultiplier(EnumSpellStat.COST, 1);
		super.addToMetadata(meta);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (context.caster instanceof MPUCaster) Errors.mpu.runtime();
		if (!context.tool.isEmpty()) Errors.runtime(SpellRuntimeException.CAD_CASTING_ONLY);
		PlayerData playerData = PlayerDataHandler.get(context.caster);
		if (context.focalPoint == context.caster
				&& context.castFrom == playerData.loopcastHand && playerData.loopcasting) {
			playerData.stopLoopcast();
		}
		if (context.focalPoint instanceof EntitySpellCircle) Errors.nestedLoop.runtime();
		// Vector3 positionVal = ParamHelper.inRange(this, context, position);
		for (int i = 1; i < 20; i++) {
			SpellContext copy = ContextHelper.fork(context);
			copy.loopcastIndex = i;
			copy.delay = 5 * i;
			PsiAPI.internalHandler.delayContext(copy);
		}
		context.loopcastIndex = 0;
		return null;
	}
	
}
