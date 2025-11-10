package gdavid.phi.spell.selector.cable;

import gdavid.phi.block.tile.VSUTile;
import gdavid.phi.cable.PeripheralContext;
import gdavid.phi.spell.Errors;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceSelector;

public class ReadVectorStorageSelector extends PieceSelector {
	
	SpellParam<Vector3> direction;
	
	public ReadVectorStorageSelector(Spell spell) {
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
		var tile = PeripheralContext.get(context).getPeripheral(VSUTile.class, dir);
		Errors.runtimeNull(tile);
		return tile.getVector();
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Vector3.class;
	}
	
}
