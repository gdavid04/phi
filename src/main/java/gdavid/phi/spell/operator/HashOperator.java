package gdavid.phi.spell.operator;

import gdavid.phi.spell.Errors;
import net.minecraft.entity.Entity;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Any;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;
import vazkii.psi.api.spell.piece.PieceOperator;

public class HashOperator extends PieceOperator {
	
	SpellParam<Any> value;
	
	public HashOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(value = new ParamAny(SpellParam.GENERIC_NAME_TARGET, SpellParam.BLUE, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Number.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Object val = getRawParamValue(context, value);
		if (val == null) Errors.runtime(SpellRuntimeException.NULL_TARGET);
		if (val instanceof Entity) return ((Entity) val).getUniqueID().hashCode();
		// String, Number and Vector3 implement hashCode properly
		// TODO EntityListWrapper support
		return val.hashCode();
	}
	
}
