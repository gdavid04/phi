package gdavid.phi.mixin;

import com.google.common.collect.Multimap;
import gdavid.phi.spell.Errors;
import gdavid.phi.util.IWarpRedirector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellGrid;
import vazkii.psi.api.spell.SpellGrid.SpellPieceConsumer;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;

@Mixin(value = SpellGrid.class, remap = false)
public abstract class SpellGridMixin {
	
	@Shadow
	protected abstract SpellPiece getPieceAtSide(Multimap<SpellPiece, Side> traversed, int x, int y, Side side)
			throws SpellCompilationException;
	
	@Redirect(method = "getPieceAtSideWithRedirections(IILvazkii/psi/api/spell/SpellParam$Side;Lvazkii/psi/api/spell/SpellGrid$SpellPieceConsumer;)Lvazkii/psi/api/spell/SpellPiece;", at = @At(value = "INVOKE", target = "vazkii/psi/api/spell/SpellGrid.getPieceAtSide(Lcom/google/common/collect/Multimap;IILvazkii/psi/api/spell/SpellParam$Side;)Lvazkii/psi/api/spell/SpellPiece;", remap = false))
	private SpellPiece advancedRedirects(SpellGrid grid, Multimap<SpellPiece, Side> traversed, int x, int y, Side side,
			int ox, int oy, Side oside, SpellPieceConsumer walker) throws SpellCompilationException {
		SpellPiece piece = ((SpellGridMixin) (Object) grid).getPieceAtSide(traversed, x, y, side);
		while (piece instanceof IWarpRedirector) {
			walker.accept(piece);
			piece = ((IWarpRedirector) piece).redirect(side);
			if (!traversed.put(piece, side)) Errors.compile(SpellCompilationException.INFINITE_LOOP);
		}
		return piece;
	}
	
}
