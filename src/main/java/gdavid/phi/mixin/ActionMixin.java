package gdavid.phi.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.util.EvalHelper;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

@Mixin(value = CompiledSpell.Action.class, remap = false)
public class ActionMixin {
	
	@Shadow
	@Final
	public SpellPiece piece;
	
	@Inject(method = "execute", at = @At("RETURN"))
	private void execute(IPlayerData data, SpellContext context, CallbackInfo callback) throws SpellRuntimeException {
		if (context.caster instanceof MPUCaster) {
			((MPUCaster) context.caster).complexityDelay(context, EvalHelper.complexity(piece));
		}
	}
	
}
