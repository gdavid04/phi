package gdavid.phi.api;

import vazkii.psi.api.spell.SpellContext;

/**
 * For data that needs special handling when forking a {@link SpellContext}
 */
public interface CustomForkData {
	
	/**
	 * Called when a {@link SpellContext} containing the instance is forked
	 * @return The {@link Object} to be put in the forked context
	 */
	public Object fork();
	
}
