package gdavid.phi.spell.trick.cable;

import gdavid.phi.block.tile.VSUTile;
import gdavid.phi.cable.PeripheralContext;
import gdavid.phi.spell.Errors;
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

public class WriteVectorStorageTrick extends PieceTrick {
	
	SpellParam<Vector3> direction;
	SpellParam<Vector3> vector;
	
	public WriteVectorStorageTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
		addParam(vector = new ParamVector(SpellParam.GENERIC_NAME_VECTOR, SpellParam.RED, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
		meta.addStat(EnumSpellStat.POTENCY, 20);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 dir = getNonnullParamValue(context, direction);
		Vector3 vec = getNonnullParamValue(context, vector);
		var tile = PeripheralContext.get(context).getPeripheral(VSUTile.class, dir);
		Errors.runtimeNull(tile);
		tile.setVector(vec);
		return null;
	}
	
}
