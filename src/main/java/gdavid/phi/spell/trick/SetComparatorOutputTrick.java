package gdavid.phi.spell.trick;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.spell.ModPieces;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class SetComparatorOutputTrick extends PieceTrick {
	
	SpellParam<Number> num;
	
	public SetComparatorOutputTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(num = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.BLUE, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
		meta.addStat(EnumSpellStat.POTENCY, 5);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (!(context.caster instanceof MPUCaster)) {
			throw new SpellRuntimeException(ModPieces.Errors.noMpu);
		}
		((MPUCaster) context.caster).setComparatorSignal(getNonnullParamValue(context, num).intValue());
		return null;
	}
	
}
