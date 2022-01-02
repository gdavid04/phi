package gdavid.phi.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gdavid.phi.Phi;
import gdavid.phi.entity.MarkerEntity;
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
public class MarkerRenderer extends EntityRenderer<MarkerEntity> {
	
	static final RenderType layer = RenderType.makeType(Phi.modId + ":" + MarkerEntity.id,
			DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 1440, false, false,
			RenderType.State.getBuilder()
					.texture(new RenderState.TextureState(new ResourceLocation(Phi.modId, "textures/entity/marker.png"),
							false, false))
					.cull(new RenderState.CullState(false)).depthTest(new RenderState.DepthTestState("always", 519))
					.transparency(new RenderState.TransparencyState("lightning_transparency", () -> {
						RenderSystem.enableBlend();
						RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
					}, () -> {
						RenderSystem.disableBlend();
						RenderSystem.defaultBlendFunc();
					})).lightmap(new RenderState.LightmapState(false)).build(true));
	
	public MarkerRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}
	
	@Override
	@SuppressWarnings("resource")
	public void render(MarkerEntity entity, float entityYaw, float partialTicks, MatrixStack ms,
			IRenderTypeBuffer buffers, int light) {
		EntityDataManager dm = entity.getDataManager();
		UUID uuid = dm.get(MarkerEntity.owner).get();
		if (!Minecraft.getInstance().player.getUniqueID().equals(uuid)) return;
		IVertexBuilder buffer = buffers.getBuffer(layer);
		float halfSize = 0.5f;
		ms.push();
		ms.translate(0, 0.5, 0);
		ms.rotate(renderManager.getCameraOrientation());
		ms.rotate(Vector3f.YP.rotationDegrees(180));
		Matrix4f mat = ms.getLast().getMatrix();
		buffer.pos(mat, -halfSize, +halfSize, 0).color(255, 255, 255, 255).tex(0, 1).lightmap(light).endVertex();
		buffer.pos(mat, +halfSize, +halfSize, 0).color(255, 255, 255, 255).tex(1, 1).lightmap(light).endVertex();
		buffer.pos(mat, +halfSize, -halfSize, 0).color(255, 255, 255, 255).tex(1, 0).lightmap(light).endVertex();
		buffer.pos(mat, -halfSize, -halfSize, 0).color(255, 255, 255, 255).tex(0, 0).lightmap(light).endVertex();
		ms.pop();
	}
	
	@Override
	public ResourceLocation getEntityTexture(MarkerEntity entity) {
		return null;
	}
	
}
