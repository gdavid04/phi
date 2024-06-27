package gdavid.phi.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import gdavid.phi.gui.VisorHUD;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	
	@Shadow @Final private RenderBuffers renderBuffers;
	
	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;applyModelViewMatrix()V", shift = Shift.AFTER, ordinal = 1))
	private void onRenderLevel(PoseStack ms, float partialTicks, long nanoTime, boolean drawBlockOutline, Camera camera, GameRenderer renderer, LightTexture lightTexture, Matrix4f proj, CallbackInfo ci) {
		VisorHUD.renderWorld(ms, renderBuffers, camera, partialTicks);
	}
	
}
