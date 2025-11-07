package gdavid.phi.spell.selector.mpu;

import gdavid.phi.block.tile.TextSUTile;
import gdavid.phi.cable.PeripheralContext;
import gdavid.phi.spell.Errors;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceSelector;

public class ReadTextStorageSelector extends PieceSelector {
	
	SpellParam<Vector3> direction;
	
	public ReadTextStorageSelector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		meta.addStat(EnumSpellStat.POTENCY, 10);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 dir = getNonnullParamValue(context, direction);
		var tile = PeripheralContext.get(context).getPeripheral(TextSUTile.class, dir);
		Errors.runtimeNull(tile);
		return tile.getText();
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
}
