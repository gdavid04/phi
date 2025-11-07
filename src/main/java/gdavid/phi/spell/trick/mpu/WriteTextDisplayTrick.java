package gdavid.phi.spell.trick.mpu;

import gdavid.phi.block.tile.TextDisplayTile;
import gdavid.phi.cable.PeripheralContext;
import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.TextParam;
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

public class WriteTextDisplayTrick extends PieceTrick {
	
	SpellParam<Vector3> direction;
	SpellParam<String> text;
	SpellParam<Number> line;
	
	public WriteTextDisplayTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
		addParam(text = new TextParam(Param.text.name, SpellParam.RED, false, false));
		addParam(line = new ParamNumber(Param.line.name, SpellParam.BLUE, true, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
		meta.addStat(EnumSpellStat.POTENCY, 5);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 dir = getNonnullParamValue(context, direction);
		String str = getNonnullParamValue(context, text);
		var tile = PeripheralContext.get(context).getPeripheral(TextDisplayTile.class, dir);
		Errors.runtimeNull(tile);
		if (paramSides.get(line).isEnabled()) {
			tile.setLine(str, getNonnullParamValue(context, line).intValue());
		} else {
			tile.appendLine(str);
		}
		return null;
	}
	
}
