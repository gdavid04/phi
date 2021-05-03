package gdavid.phi.mixin;

import java.util.EnumSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gdavid.phi.util.ReferenceParam;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;

@Pseudo
@Mixin(targets = "vazkii.psi.common.spell.SpellCompiler", remap = false)
public class SpellCompilerMixin {
	
	@Inject(method = "checkSideDisabled(Lvazkii/psi/api/spell/SpellParam;Lvazkii/psi/api/spell/SpellPiece;Ljava/util/EnumSet;)Z", at = @At("RETURN"), cancellable = true)
	private void checkSideDisabled(SpellParam<?> param, SpellPiece parent, EnumSet<SpellParam.Side> seen, CallbackInfoReturnable<Boolean> callback) throws SpellCompilationException {
		if (param instanceof ReferenceParam) {
			callback.setReturnValue(true);
		}
	}
	
}
