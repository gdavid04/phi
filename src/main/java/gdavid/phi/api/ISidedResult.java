package gdavid.phi.api;

import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellRuntimeException;

/**
 * Returned from pieces that can return different values on each side.
 */
public interface ISidedResult {
	
	Object get(Side side) throws SpellRuntimeException;
	
}
