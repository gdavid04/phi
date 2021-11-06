package gdavid.phi.mixin;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import java.lang.ref.WeakReference;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "vazkii.psi.common.core.handler.PlayerDataHandler$PlayerData", remap = false)
public class PlayerDataMixin {
	
	@Final
	public WeakReference<PlayerEntity> playerWR;
	
	@Inject(method = "deductPsi(IIZZ)V", at = @At("HEAD"), cancellable = true)
	private void deductPsi(int psi, int cd, boolean sync, boolean shatter, CallbackInfo callback) {
		PlayerEntity player = playerWR.get();
		if (player instanceof MPUCaster) {
			((MPUCaster) player).deductPsi(psi, cd);
			callback.cancel();
		}
	}
	
	@Inject(method = "getAvailablePsi", at = @At("HEAD"), cancellable = true)
	private void getAvailablePsi(CallbackInfoReturnable<Integer> callback) {
		PlayerEntity player = playerWR.get();
		if (player instanceof MPUCaster) {
			callback.setReturnValue(((MPUCaster) player).getPsi());
		}
	}
	
	@Inject(method = "getLastAvailablePsi", at = @At("HEAD"), cancellable = true)
	private void getLastAvailablePsi(CallbackInfoReturnable<Integer> callback) {
		PlayerEntity player = playerWR.get();
		if (player instanceof MPUCaster) {
			callback.setReturnValue(((MPUCaster) player).getPsi());
		}
	}
	
	@Inject(method = "getTotalPsi", at = @At("HEAD"), cancellable = true)
	private void getTotalPsi(CallbackInfoReturnable<Integer> callback) {
		PlayerEntity player = playerWR.get();
		if (player instanceof MPUCaster) {
			callback.setReturnValue(((MPUCaster) player).getMaxPsi());
		}
	}
	
}
