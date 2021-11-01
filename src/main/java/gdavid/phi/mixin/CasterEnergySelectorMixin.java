package gdavid.phi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import vazkii.psi.api.spell.SpellContext;

@Pseudo
@Mixin(targets = "vazkii.psi.common.spell.selector.entity.PieceSelectorCasterEnergy", remap = false)
public class CasterEnergySelectorMixin {
	
	@Inject(method = "execute", at = @At("HEAD"), cancellable = true)
	private void execute(SpellContext context, CallbackInfoReturnable<Object> callback) {
		if (context.caster instanceof MPUCaster) {
			callback.setReturnValue(((MPUCaster) context.caster).getPsi());
		}
	}
	
}
