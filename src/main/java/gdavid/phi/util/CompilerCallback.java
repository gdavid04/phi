package gdavid.phi.util;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import gdavid.phi.api.ICustomCompile.ICompilerCallback;
import gdavid.phi.spell.Errors;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.common.spell.SpellCompiler;

public class CompilerCallback implements ICompilerCallback {
	
	final SpellCompiler compiler;
	final SpellPiece piece;
	final Set<SpellPiece> visited;
	final ICheckSideDisabled checkSideDisabled; // fucking IllegalAccessExceptions
	
	public CompilerCallback(SpellCompiler compiler, SpellPiece piece, Set<SpellPiece> visited, ICheckSideDisabled checkSideDisabled) {
		this.compiler = compiler;
		this.piece = piece;
		this.visited = visited;
		this.checkSideDisabled = checkSideDisabled;
	}
	
	@Override
	public void withVisited(SpellPiece piece, Set<SpellPiece> addToVisited)
			throws SpellCompilationException {
		Set<SpellPiece> visitedCopy = new HashSet<>(visited);
		if (addToVisited != null) visitedCopy.addAll(addToVisited);
		compiler.buildPiece(piece, visitedCopy);
	}
	
	@Override
	public SpellPiece param(SpellParam<?> param, EnumSet<Side> usedSides) throws SpellCompilationException {
		if (checkSideDisabled.check(param, piece, usedSides)) return null;
		Side side = piece.paramSides.get(param);
		SpellPiece other = ((CompiledSpell) ObfuscationReflectionHelper.getPrivateValue(SpellCompiler.class, compiler, "compiled"))
				.sourceSpell.grid.getPieceAtSideWithRedirections(piece.x, piece.y, side, compiler::buildRedirect);
		if (other == null) Errors.compile(SpellCompilationException.NULL_PARAM);
		if (!param.canAccept(other)) Errors.compile(SpellCompilationException.INVALID_PARAM);
		return other;
	}
	
	@FunctionalInterface
	public interface ICheckSideDisabled {
		
		public boolean check(SpellParam<?> param, SpellPiece parent, EnumSet<Side> seen) throws SpellCompilationException;
		
	}
	
}
