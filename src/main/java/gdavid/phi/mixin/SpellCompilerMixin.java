package gdavid.phi.mixin;

import gdavid.phi.spell.other.JumpConnector;
import gdavid.phi.util.ReferenceParam;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;

@Pseudo
@Mixin(targets = "vazkii.psi.common.spell.SpellCompiler", remap = false)
public class SpellCompilerMixin {
	
	@Inject(method = "checkSideDisabled(Lvazkii/psi/api/spell/SpellParam;Lvazkii/psi/api/spell/SpellPiece;Ljava/util/EnumSet;)Z", at = @At("RETURN"), cancellable = true)
	private void checkSideDisabled(SpellParam<?> param, SpellPiece parent, EnumSet<SpellParam.Side> seen,
			CallbackInfoReturnable<Boolean> callback) throws SpellCompilationException {
		if (param instanceof ReferenceParam) {
			callback.setReturnValue(true);
		}
	}
	
	@Shadow
	public void buildPiece(SpellPiece piece, Set<SpellPiece> visited) throws SpellCompilationException {
	}
	
	@Inject(method = "buildPiece(Lvazkii/psi/api/spell/SpellPiece;Ljava/util/Set;)V", at = @At("RETURN"))
	private void buildPieceOverride(SpellPiece piece, Set<SpellPiece> visited,
			CallbackInfo callback) throws SpellCompilationException {
		System.out.print(piece);
		if (piece instanceof JumpConnector) {
			buildPiece(((JumpConnector) piece).getTarget(), new HashSet<>(visited));
		}
	}
	
}
