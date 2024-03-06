package gdavid.phi.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import gdavid.phi.Phi;
import gdavid.phi.entity.SpiritEntity;
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
public class SpiritRenderer extends EntityRenderer<SpiritEntity> {
	
	static final RenderType layer = RenderType.create(Phi.modId + ":" + SpiritEntity.id,
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, QUADS, 1440, false, false,
			RenderType.CompositeState.builder()
					.setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(Phi.modId, "textures/entity/spirit.png"),
							false, false))
					.setCullState(new RenderStateShard.CullStateShard(false))
					.setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
						RenderSystem.enableBlend();
						RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
								GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
								GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					}, () -> {
						RenderSystem.disableBlend();
						RenderSystem.defaultBlendFunc();
					})).setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
					.setLightmapState(new RenderStateShard.LightmapStateShard(true)).createCompositeState(true));
	
	public SpiritRenderer(Context context) {
		super(context);
	}
	
	@Override
	@SuppressWarnings("resource")
	public void render(SpiritEntity entity, float entityYaw, float partialTicks, PoseStack ms,
			MultiBufferSource buffers, int light) {
		SynchedEntityData dm = entity.getEntityData();
		UUID uuid = dm.get(SpiritEntity.owner).get();
		float tx = Minecraft.getInstance().player.getUUID().equals(uuid) ? 0.5f : 0;
		int fullbright = 0xF000F0;
		VertexConsumer buffer = buffers.getBuffer(layer);
		float halfSize = 0.4f;
		ms.pushPose();
		ms.translate(0, halfSize, 0);
		ms.mulPose(entityRenderDispatcher.cameraOrientation());
		ms.mulPose(Vector3f.YP.rotationDegrees(180));
		Matrix4f mat = ms.last().pose();
		buffer.vertex(mat, -halfSize, +halfSize, 0).color(255, 255, 255, 255).uv(tx, 0.5f).uv2(fullbright)
				.endVertex();
		buffer.vertex(mat, +halfSize, +halfSize, 0).color(255, 255, 255, 255).uv(tx + 0.5f, 0.5f).uv2(fullbright)
				.endVertex();
		buffer.vertex(mat, +halfSize, -halfSize, 0).color(255, 255, 255, 255).uv(tx + 0.5f, 0).uv2(fullbright)
				.endVertex();
		buffer.vertex(mat, -halfSize, -halfSize, 0).color(255, 255, 255, 255).uv(tx, 0).uv2(fullbright).endVertex();
		ms.popPose();
	}
	
	@Override
	public ResourceLocation getTextureLocation(SpiritEntity entity) {
		return null;
	}
	
}
