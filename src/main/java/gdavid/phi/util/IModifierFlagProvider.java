package gdavid.phi.util;

import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellMetadata;

public interface IModifierFlagProvider {
	
	void addFlags(SpellMetadata meta) throws SpellCompilationException;
	
}
