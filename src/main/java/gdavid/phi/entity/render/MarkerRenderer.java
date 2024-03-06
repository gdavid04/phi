package gdavid.phi.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import gdavid.phi.Phi;
import gdavid.phi.entity.MarkerEntity;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import static com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS;

@OnlyIn(Dist.CLIENT)
public class MarkerRenderer extends EntityRenderer<MarkerEntity> {
	
	static final RenderType layer = RenderType.create(Phi.modId + ":" + MarkerEntity.id,
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, QUADS, 1440, false, false,
			RenderType.CompositeState.builder()
					.setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(Phi.modId, "textures/entity/marker.png"),
							false, false))
					.setCullState(new RenderStateShard.CullStateShard(false)).setDepthTestState(new RenderStateShard.DepthTestStateShard("always", 519))
					.setTransparencyState(new RenderStateShard.TransparencyStateShard("lightning_transparency", () -> {
						RenderSystem.enableBlend();
						RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
					}, () -> {
						RenderSystem.disableBlend();
						RenderSystem.defaultBlendFunc();
					})).setLightmapState(new RenderStateShard.LightmapStateShard(false)).createCompositeState(true));
	
	public MarkerRenderer(Context context) {
		super(context);
	}
	
	@Override
	public void render(MarkerEntity entity, float entityYaw, float partialTicks, PoseStack ms,
			MultiBufferSource buffers, int light) {
		SynchedEntityData dm = entity.getEntityData();
		UUID uuid = dm.get(MarkerEntity.owner).get();
		if (!Minecraft.getInstance().player.getUUID().equals(uuid)) return;
		int fullbright = 0xF000F0;
		VertexConsumer buffer = buffers.getBuffer(layer);
		float halfSize = 0.5f;
		ms.pushPose();
		ms.translate(0, 0.5, 0);
		ms.mulPose(entityRenderDispatcher.cameraOrientation());
		ms.mulPose(Vector3f.YP.rotationDegrees(180));
		Matrix4f mat = ms.last().pose();
		buffer.vertex(mat, -halfSize, +halfSize, 0).color(255, 255, 255, 255).uv(0, 1).uv2(fullbright).endVertex();
		buffer.vertex(mat, +halfSize, +halfSize, 0).color(255, 255, 255, 255).uv(1, 1).uv2(fullbright).endVertex();
		buffer.vertex(mat, +halfSize, -halfSize, 0).color(255, 255, 255, 255).uv(1, 0).uv2(fullbright).endVertex();
		buffer.vertex(mat, -halfSize, -halfSize, 0).color(255, 255, 255, 255).uv(0, 0).uv2(fullbright).endVertex();
		ms.popPose();
	}
	
	@Override
	public ResourceLocation getTextureLocation(MarkerEntity entity) {
		return null;
	}
	
}
