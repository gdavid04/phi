package gdavid.phi.mixin;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.spell.error.PropagatingSpellRuntimeException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.CompiledSpell.Action;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.common.network.message.MessageSpellError;

@Mixin(value = CompiledSpell.class, remap = false)
public class CompiledSpellMixin {
	
	private static ThreadLocal<SpellRuntimeException> exception = new ThreadLocal<>();
	
	@Redirect(method = "safeExecute", at = @At(value = "NEW", target = "(Ljava/lang/String;II)Lvazkii/psi/common/network/message/MessageSpellError;"))
	private MessageSpellError errorPos(String message, int x, int y) {
		if (exception.get() instanceof PropagatingSpellRuntimeException) {
			PropagatingSpellRuntimeException e = (PropagatingSpellRuntimeException) exception.get();
			x = e.x + 1;
			y = e.y + 1;
		}
		return new MessageSpellError(message, x, y);
	}
	
	@Inject(method = "safeExecute", at = @At(value = "INVOKE", target = "Lvazkii/psi/api/spell/SpellContext;shouldSuppressErrors()Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void failExecute(SpellContext context, CallbackInfo callback, SpellRuntimeException e) {
		// assume this is called if and only if an error occurs
		exception.set(e);
		if (context.caster instanceof MPUCaster) {
			((MPUCaster) context.caster).fail();
		}
	}
	
	@Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lvazkii/psi/api/spell/CompiledSpell$Action;execute(Lvazkii/psi/api/internal/IPlayerData;Lvazkii/psi/api/spell/SpellContext;)V"))
	private void executeAction(Action action, IPlayerData data, SpellContext context) throws SpellRuntimeException {
		try {
			action.execute(data, context);
		} catch (SpellRuntimeException e) {
			SpellPiece piece = action.piece;
			boolean suppress = context.cspell.metadata.getFlag(PropagatingSpellRuntimeException.suppressFlag(piece));
			PropagatingSpellRuntimeException pe = e instanceof PropagatingSpellRuntimeException ? (PropagatingSpellRuntimeException) e : null;
			if (pe != null && pe.rethrown) throw e;
			if (pe != null && (pe.propagate || suppress)) context.evaluatedObjects[piece.x][piece.y] = e;
			else if (suppress) context.evaluatedObjects[piece.x][piece.y] =
					new PropagatingSpellRuntimeException(e.getMessage(), piece.x, piece.y, false, false);
			else throw e;
		}
	}
	
}
