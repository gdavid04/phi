package gdavid.phi.mixin;

import gdavid.phi.spell.param.ReferenceParam;
import gdavid.phi.util.IModifierFlagProvider;

import java.util.EnumSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellGrid;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;

@Pseudo
@Mixin(targets = "vazkii.psi.common.spell.SpellCompiler", remap = false)
public class SpellCompilerMixin {
	
	@Inject(method = "doCompile", at = @At("RETURN"))
	private void doCompile(Spell spell, CallbackInfoReturnable<CompiledSpell> callback) throws SpellCompilationException {
		for (int x = 0; x < SpellGrid.GRID_SIZE; x++) {
			for (int y = 0; y < SpellGrid.GRID_SIZE; y++) {
				SpellPiece piece = spell.grid.gridData[x][y];
				if (piece instanceof IModifierFlagProvider) {
					((IModifierFlagProvider) piece).addFlags(callback.getReturnValue().metadata);
				}
			}
		}
	}
	
	@Inject(method = "checkSideDisabled", at = @At("RETURN"), cancellable = true)
	private void checkSideDisabled(SpellParam<?> param, SpellPiece parent, EnumSet<SpellParam.Side> seen,
			CallbackInfoReturnable<Boolean> callback) throws SpellCompilationException {
		if (param instanceof ReferenceParam) {
			callback.setReturnValue(true);
		}
	}
	
}
