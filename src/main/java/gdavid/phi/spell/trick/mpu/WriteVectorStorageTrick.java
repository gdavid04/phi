package gdavid.phi.spell.trick.mpu;

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
		Direction d = Direction.getFacingFromVector(dir.x, dir.y, dir.z);
		Vector3 vec = getNonnullParamValue(context, vector);
		if (!(context.caster instanceof MPUCaster)) {
			throw new SpellRuntimeException(ModPieces.Errors.noMpu);
		}
		TileEntity tile = context.caster.world.getTileEntity(context.caster.getPosition().add(d.getDirectionVec()));
		if (!(tile instanceof VSUTile)) {
			throw new SpellRuntimeException(SpellRuntimeException.NULL_TARGET);
		}
		((VSUTile) tile).setVector(vec);
		return null;
	}
	
}
