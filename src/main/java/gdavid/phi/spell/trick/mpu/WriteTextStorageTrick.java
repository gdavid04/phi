package gdavid.phi.spell.trick.mpu;

import gdavid.phi.block.tile.TextSUTile;
import gdavid.phi.cable.PeripheralContext;
import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.TextParam;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class WriteTextStorageTrick extends PieceTrick {
	
	SpellParam<Vector3> direction;
	SpellParam<String> text;
	
	public WriteTextStorageTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
		addParam(text = new TextParam(Param.text.name, SpellParam.RED, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
		meta.addStat(EnumSpellStat.POTENCY, 20);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 dir = getNonnullParamValue(context, direction);
		String str = getNonnullParamValue(context, text);
		var tile = PeripheralContext.get(context).getPeripheral(TextSUTile.class, dir);
		Errors.runtimeNull(tile);
		tile.setText(str);
		return null;
	}
	
}
