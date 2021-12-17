package gdavid.phi.spell.selector.mpu;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.block.tile.VSUTile;
import gdavid.phi.spell.ModPieces;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
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
		Direction d = Direction.getFacingFromVector(dir.x, dir.y, dir.z);
		if (!(context.caster instanceof MPUCaster)) {
			throw new SpellRuntimeException(ModPieces.Errors.noMpu);
		}
		TileEntity tile = context.caster.world.getTileEntity(context.caster.getPosition().add(d.getDirectionVec()));
		if (!(tile instanceof VSUTile)) {
			throw new SpellRuntimeException(SpellRuntimeException.NULL_TARGET);
		}
		return ((VSUTile) tile).getVector();
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Vector3.class;
	}
	
}
