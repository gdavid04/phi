package gdavid.phi.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gdavid.phi.Phi;
import gdavid.phi.entity.PsiProjectileEntity;
import gdavid.phi.util.RenderHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import vazkii.psi.api.PsiAPI;

@OnlyIn(Dist.CLIENT)
public class PsiProjectileRenderer extends EntityRenderer<PsiProjectileEntity> {
	
	static final RenderType layer = RenderType.makeType(Phi.modId + ":" + PsiProjectileEntity.id,
			DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 1440, false, false,
			RenderType.State.getBuilder()
					.texture(new RenderState.TextureState(
							new ResourceLocation(PsiAPI.MOD_ID, "textures/particle/wisp.png"), false, false))
					.cull(new RenderState.CullState(false))
					.transparency(new RenderState.TransparencyState("lightning_transparency", () -> {
						RenderSystem.enableBlend();
						RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
					}, () -> {
						RenderSystem.disableBlend();
						RenderSystem.defaultBlendFunc();
					})).writeMask(new RenderState.WriteMaskState(true, false))
					.lightmap(new RenderState.LightmapState(true)).build(true));
	
	public PsiProjectileRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void render(PsiProjectileEntity entity, float entityYaw, float partialTicks, MatrixStack ms,
			IRenderTypeBuffer buffers, int light) {
		EntityDataManager dm = entity.getDataManager();
		ItemStack colorizer = dm.get(PsiProjectileEntity.colorizer);
		int color = RenderHelper.getColorForColorizer(colorizer);
		int r = RenderHelper.r(color);
		int g = RenderHelper.g(color);
		int b = RenderHelper.b(color);
		IVertexBuilder buffer = buffers.getBuffer(layer);
		int fullbright = 0xF000F0;
		float halfSize = 0.2f * (float) Math.min(1, Math.sqrt(dm.get(PsiProjectileEntity.psi) / 350f));
		ms.push();
		ms.rotate(renderManager.getCameraOrientation());
		ms.rotate(Vector3f.YP.rotationDegrees(180));
		Matrix4f mat = ms.getLast().getMatrix();
		buffer.pos(mat, -halfSize, +halfSize, 0).color(r, g, b, 255).tex(0, 1).lightmap(fullbright).endVertex();
		buffer.pos(mat, +halfSize, +halfSize, 0).color(r, g, b, 255).tex(1, 1).lightmap(fullbright).endVertex();
		buffer.pos(mat, +halfSize, -halfSize, 0).color(r, g, b, 255).tex(1, 0).lightmap(fullbright).endVertex();
		buffer.pos(mat, -halfSize, -halfSize, 0).color(r, g, b, 255).tex(0, 0).lightmap(fullbright).endVertex();
		ms.pop();
	}
	
	@Override
	public ResourceLocation getEntityTexture(PsiProjectileEntity entity) {
		return null;
	}
	
}
