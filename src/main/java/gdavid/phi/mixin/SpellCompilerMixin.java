package gdavid.phi.mixin;

import java.util.EnumSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gdavid.phi.api.ICustomCompile;
import gdavid.phi.api.param.ReferenceParam;
import gdavid.phi.spell.Errors;
import gdavid.phi.util.CompilerCallback;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.common.spell.SpellCompiler;

@Mixin(value = SpellCompiler.class, remap = false)
public abstract class SpellCompilerMixin {
	
	@Shadow
	private CompiledSpell compiled;
	
	@Shadow
	public abstract void buildPiece(SpellPiece piece, Set<SpellPiece> visited) throws SpellCompilationException;
	
	@Shadow
	public abstract boolean checkSideDisabled(SpellParam<?> param, SpellPiece parent, EnumSet<Side> seen) throws SpellCompilationException;
	
	@Inject(method = "buildPiece(Lvazkii/psi/api/spell/SpellPiece;Ljava/util/Set;)V", at = @At("HEAD"), cancellable = true)
	private void customBuild(SpellPiece piece, Set<SpellPiece> visited, CallbackInfo callback) throws SpellCompilationException {
		if (piece instanceof ICustomCompile) {
			callback.cancel();
			if (!visited.add(piece)) Errors.compile(SpellCompilationException.INFINITE_LOOP, piece.x, piece.y);
			((ICustomCompile) piece).compile(compiled, new CompilerCallback((SpellCompiler) (Object) this, piece, visited, this::checkSideDisabled));
		}
	}
	
	@Inject(method = "checkSideDisabled", at = @At("RETURN"), cancellable = true)
	private void ignoreReferenceParam(SpellParam<?> param, SpellPiece parent, EnumSet<Side> seen,
			CallbackInfoReturnable<Boolean> callback) throws SpellCompilationException {
		if (param instanceof ReferenceParam) {
			callback.setReturnValue(true);
		}
	}
	
}
