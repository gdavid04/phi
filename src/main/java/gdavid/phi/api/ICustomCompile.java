package gdavid.phi.api;

import java.util.EnumSet;
import java.util.Set;

import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.CompiledSpell.Action;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;

/**
 * For pieces that require completely different compiler logic.
 * You must do param checks, add actions and build parameters yourself.
 * Implementations must extend {@link SpellPiece}
 */
public interface ICustomCompile {
	
	/**
	 * Called when the piece is compiled
	 * @param compiled the {@link CompiledSpell} being compiled
	 * @param cb compiler callbacks
	 * @throws SpellCompilationException
	 */
	public default void compile(CompiledSpell compiled, ICompilerCallback cb) throws SpellCompilationException {
		if (compiled.actionMap.containsKey((SpellPiece) this)) {
			Action a = compiled.actionMap.get((SpellPiece) this);
			compiled.actions.remove(a);
			compiled.actions.add(a);
		} else {
			Action a = compiled.new Action((SpellPiece) this);
			compiled.actions.add(a);
			compiled.actionMap.put((SpellPiece) this, a);
			((SpellPiece) this).addToMetadata(compiled.metadata);
		}
	}
	
	public interface ICompilerCallback {
		
		public default void build(SpellPiece piece) throws SpellCompilationException {
			withVisited(piece, null);
		}
		
		public default void buildOptional(SpellPiece piece) throws SpellCompilationException {
			if (piece != null) build(piece);
		}
		
		public void withVisited(SpellPiece piece, Set<SpellPiece> addToVisited) throws SpellCompilationException;
		
		public default void optionalWithVisited(SpellPiece piece, Set<SpellPiece> addToVisited) throws SpellCompilationException {
			if (piece != null) withVisited(piece, addToVisited);
		}
		
		public SpellPiece param(SpellParam<?> param, EnumSet<Side> usedSides) throws SpellCompilationException;
		
	}
	
}
