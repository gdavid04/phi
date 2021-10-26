package gdavid.phi.util;

import vazkii.psi.api.spell.param.ParamAny;

/**
 * A parameter used for referencing other pieces. Execution order between the
 * piece and the parameter is undefined.
 */
public class ReferenceParam extends ParamAny {
	
	public boolean checkLoop = false;
	
	public ReferenceParam(String name, int color, boolean canDisable) {
		super(name, color, canDisable);
	}
	
	public ReferenceParam(String name, int color, boolean canDisable, ArrowType arrowType) {
		super(name, color, canDisable, arrowType);
	}
	
	/**
	 * Use this when the target can be evaluated regardless of the action stack.
	 * Loops should be checked for in the piece during metadata calculation.
	 */
	public ReferenceParam preventLoop() {
		checkLoop = true;
		return this;
	}
	
}
