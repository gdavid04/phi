package gdavid.phi.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gdavid.phi.Phi;
import gdavid.phi.entity.SpiritEntity;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class SpiritRenderer extends EntityRenderer<SpiritEntity> {
	
	static final RenderType layer = RenderType.makeType(Phi.modId + ":" + SpiritEntity.id,
			DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 1440, false, false,
			RenderType.State.getBuilder()
					.texture(new RenderState.TextureState(new ResourceLocation(Phi.modId, "textures/entity/spirit.png"),
							false, false))
					.cull(new RenderState.CullState(false))
					.transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
						RenderSystem.enableBlend();
						RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
								GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
								GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					}, () -> {
						RenderSystem.disableBlend();
						RenderSystem.defaultBlendFunc();
					})).writeMask(new RenderState.WriteMaskState(true, false))
					.lightmap(new RenderState.LightmapState(true)).build(true));
	
	public SpiritRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}
	
	@Override
	@SuppressWarnings("resource")
	public void render(SpiritEntity entity, float entityYaw, float partialTicks, MatrixStack ms,
			IRenderTypeBuffer buffers, int light) {
		EntityDataManager dm = entity.getDataManager();
		UUID uuid = dm.get(SpiritEntity.owner).get();
		float tx = Minecraft.getInstance().player.getUniqueID().equals(uuid) ? 0.5f : 0;
		int fullbright = 0xF000F0;
		IVertexBuilder buffer = buffers.getBuffer(layer);
		float halfSize = 0.4f;
		ms.push();
		ms.translate(0, halfSize, 0);
		ms.rotate(renderManager.getCameraOrientation());
		ms.rotate(Vector3f.YP.rotationDegrees(180));
		Matrix4f mat = ms.getLast().getMatrix();
		buffer.pos(mat, -halfSize, +halfSize, 0).color(255, 255, 255, 255).tex(tx, 0.5f).lightmap(fullbright)
				.endVertex();
		buffer.pos(mat, +halfSize, +halfSize, 0).color(255, 255, 255, 255).tex(tx + 0.5f, 0.5f).lightmap(fullbright)
				.endVertex();
		buffer.pos(mat, +halfSize, -halfSize, 0).color(255, 255, 255, 255).tex(tx + 0.5f, 0).lightmap(fullbright)
				.endVertex();
		buffer.pos(mat, -halfSize, -halfSize, 0).color(255, 255, 255, 255).tex(tx, 0).lightmap(fullbright).endVertex();
		ms.pop();
	}
	
	@Override
	public ResourceLocation getEntityTexture(SpiritEntity entity) {
		return null;
	}
	
}
