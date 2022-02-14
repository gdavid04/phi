package gdavid.phi.mixin;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.spell.selector.entity.PieceSelectorSuccessCounter;

@Mixin(value = PieceSelectorSuccessCounter.class, remap = false)
public class SuccessCounterSelectorMixin {
	
	@Inject(method = "execute", at = @At("HEAD"), cancellable = true)
	private void execute(SpellContext context, CallbackInfoReturnable<Object> callback) {
		if (context.caster instanceof MPUCaster) {
			callback.setReturnValue(((MPUCaster) context.caster).getSuccessCount());
		}
	}
	
}
