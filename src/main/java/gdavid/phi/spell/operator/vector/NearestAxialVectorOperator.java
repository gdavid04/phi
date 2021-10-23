package gdavid.phi.spell.operator.vector;

import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class NearestAxialVectorOperator extends PieceOperator {
	
	SpellParam<Vector3> vector;
	
	public NearestAxialVectorOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(vector = new ParamVector(SpellParam.GENERIC_NAME_VECTOR, SpellParam.GREEN, false, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Vector3.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 vec = getNonnullParamValue(context, vector);
		if (Math.abs(vec.x) >= Math.abs(vec.y)) {
			if (Math.abs(vec.x) >= Math.abs(vec.z)) return new Vector3(Math.signum(vec.x), 0, 0);
			else return new Vector3(0, 0, Math.signum(vec.z));
		} else if (Math.abs(vec.y) >= Math.abs(vec.z)) return new Vector3(0, Math.signum(vec.y), 0);
		return new Vector3(0, 0, Math.signum(vec.z));
	}
	
}
