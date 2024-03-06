package gdavid.phi.spell.param;

import vazkii.psi.api.spell.param.ParamAny;

import vazkii.psi.api.spell.SpellParam.ArrowType;

/**
 * A parameter used for referencing other pieces. Execution order between the
 * piece and the parameter is undefined.
 */
public class ReferenceParam extends ParamAny {
	
	public boolean checkLoop = false;
	public boolean isOutput = false;
	
	public ReferenceParam(String name, int color, boolean canDisable, boolean isOutput) {
		super(name, color, canDisable);
		this.isOutput = isOutput;
	}
	
	public ReferenceParam(String name, int color, boolean canDisable, boolean isOutput, ArrowType arrowType) {
		super(name, color, canDisable, arrowType);
		this.isOutput = isOutput;
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
