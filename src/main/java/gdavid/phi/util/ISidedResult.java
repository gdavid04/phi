package gdavid.phi.util;

import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;

public interface ISidedResult {
	
	Object get(SpellParam.Side side) throws SpellRuntimeException;
	
}
