package gdavid.phi.api;

import vazkii.psi.api.spell.IGenericRedirector;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;

/**
 * For portal style connectors
 */
public interface IWarpRedirector {
	
	/**
	 * Gets the piece this warp redirector redirects to. An off-grid
	 * {@link IGenericRedirector} with the proper x and y coordinates set may be returned to
	 * redirect to a different side.
	 */
	SpellPiece redirect(Side side);
	
}
