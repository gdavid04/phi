package gdavid.phi.mixin;

import gdavid.phi.spell.error.PropagatingSpellRuntimeException;
import gdavid.phi.spell.param.ErrorParam;
import gdavid.phi.util.ISidedResult;
import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

@Mixin(value = SpellPiece.class, remap = false)
public class SpellPieceMixin {
	
	@Shadow
	@Final
	public Map<SpellParam<?>, SpellParam.Side> paramSides;
	
	@Shadow
	@Final
	public Spell spell;
	
	@Shadow
	public int x, y;
	
	@Inject(method = "getRawParamValue", at = @At("RETURN"), cancellable = true)
	private void getRawParamValue(SpellContext context, SpellParam<?> param, CallbackInfoReturnable<Object> callback)
			throws SpellRuntimeException {
		Object res = callback.getReturnValue();
		if (res instanceof PropagatingSpellRuntimeException && !(param instanceof ErrorParam)) {
			((PropagatingSpellRuntimeException) res).rethrow(((SpellPiece) (Object) this).getPieceType().isTrick());
		}
		SpellParam.Side side = paramSides.get(param);
		if (!side.isEnabled()) {
			return;
		}
		if (res instanceof ISidedResult) {
			try {
				SpellPiece[] piece = { (SpellPiece) (Object) this };
				SpellPiece target = spell.grid.getPieceAtSideWithRedirections(x, y, side, p -> {
					piece[0] = p;
				});
				for (SpellParam.Side s : SpellParam.Side.values()) {
					if (s.offx == piece[0].x - target.x && s.offy == piece[0].y - target.y) {
						callback.setReturnValue(((ISidedResult) res).get(s));
						break;
					}
				}
			} catch (SpellCompilationException e) {
			}
		}
	}
	
}
