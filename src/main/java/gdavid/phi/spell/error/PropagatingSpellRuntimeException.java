package gdavid.phi.spell.error;

import gdavid.phi.Phi;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

@SuppressWarnings("serial")
public class PropagatingSpellRuntimeException extends SpellRuntimeException {
	
	public final int x, y;
	
	/**
	 * Whether to propagate the error or crash the spell when used in an input that doesn't accept errors
	 */
	public final boolean propagate;
	
	/**
	 * Indicates that this error caused a crash and should not be caught
	 */
	public final boolean rethrown;
	
	public PropagatingSpellRuntimeException(String message, int x, int y, boolean propagate, boolean rethrown) {
		super(message);
		this.x = x;
		this.y = y;
		this.propagate = propagate;
		this.rethrown = rethrown;
	}
	
	/**
	 * Crashes the spell with this error or propagates it if {@link #propagate} is set
	 * @param force whether to crash the spell even if the error could propagate
	 */
	public void rethrow(boolean force) throws SpellRuntimeException {
		throw new PropagatingSpellRuntimeException(getMessage(), x, y, propagate, !propagate || force);
	}
	
	/**
	 * Metadata flag for converting crashing exceptions into propagating ones
	 */
	public static String suppressFlag(SpellPiece piece) {
		return Phi.modId + ":suppress_crash." + piece.x + "." + piece.y;
	}
	
}
