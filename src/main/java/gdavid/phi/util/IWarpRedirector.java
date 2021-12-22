package gdavid.phi.util;

import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;

public interface IWarpRedirector {
	
	/**
	 * Gets the piece this warp redirector redirects to.
	 * An off-grid IGenericRedirector with the proper x and y coordinates set
	 * may be returned to redirect to a different side.
	 */
	SpellPiece redirect(Side side);
	
}
