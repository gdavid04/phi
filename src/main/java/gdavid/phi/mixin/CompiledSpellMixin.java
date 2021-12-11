package gdavid.phi.mixin;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.SpellContext;

@Mixin(value = CompiledSpell.class, remap = false)
public class CompiledSpellMixin {
	
	@Inject(method = "safeExecute", at = @At(value = "INVOKE", target = "Lvazkii/psi/api/spell/SpellContext;shouldSuppressErrors()Z"))
	private void failExecute(SpellContext context, CallbackInfo callback) {
		// assume this is called if and only if an error occurs
		if (context.caster instanceof MPUCaster) {
			((MPUCaster) context.caster).fail();
		}
	}
	
}
