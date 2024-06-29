package gdavid.phi.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import vazkii.psi.api.spell.SpellContext;

@OnlyIn(Dist.CLIENT)
public record HUDContext(PoseStack ms, RenderBuffers buffers, Camera camera, float partialTicks) {
	
	public void setup(SpellContext context) {
		ms.pushPose();
		ms.mulPose(Vector3f.YN.rotationDegrees(180 + context.caster.getViewYRot(partialTicks)));
		ms.translate(0, 0, -1);
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void cleanup() {
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.enableCull();
		ms.popPose();
	}
	
}
