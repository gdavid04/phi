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
		
		/**
		 * Compiles another piece.
		 */
		public default void build(SpellPiece piece) throws SpellCompilationException {
			withVisited(piece, null);
		}
		
		/**
		 * Null safe version of {@link #build(SpellPiece)}.
		 */
		public default void buildOptional(SpellPiece piece) throws SpellCompilationException {
			if (piece != null) build(piece);
		}
		
		/**
		 * Compiles another piece.
		 * @param addToVisited additional pieces that trigger the infinite loop compiler error when used by the target piece
		 */
		public void withVisited(SpellPiece piece, Set<SpellPiece> addToVisited) throws SpellCompilationException;
		
		/**
		 * Null safe version of {@link #withVisited(SpellPiece, Set)}.
		 * @param addToVisited additional pieces that trigger the infinite loop compiler error when used by the target piece
		 */
		public default void optionalWithVisited(SpellPiece piece, Set<SpellPiece> addToVisited) throws SpellCompilationException {
			if (piece != null) withVisited(piece, addToVisited);
		}
		
		/**
		 * Use for getting params. Performs the same checks as the compiler does by default.
		 * @return the piece the param is connected to. You should {@link #build(SpellPiece)}
		 * (or {@link #buildOptional(SpellPiece)} for optional parameters) this before you add
		 * any action that uses the value of the parameter.
		 */
		public SpellPiece param(SpellParam<?> param, EnumSet<Side> usedSides) throws SpellCompilationException;
		
	}
	
}
