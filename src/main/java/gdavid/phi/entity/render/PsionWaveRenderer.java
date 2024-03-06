package gdavid.phi.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import gdavid.phi.Phi;
import gdavid.phi.entity.PsionWaveEntity;
import gdavid.phi.util.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import vazkii.psi.api.PsiAPI;

import static com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS;

@OnlyIn(Dist.CLIENT)
public class PsionWaveRenderer extends EntityRenderer<PsionWaveEntity> {
	
	static final RenderType layer = RenderType.create(Phi.modId + ":" + PsionWaveEntity.id,
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, QUADS, 1440, false, false,
			RenderType.CompositeState.builder()
					.setTextureState(new RenderStateShard.TextureStateShard(
							new ResourceLocation(PsiAPI.MOD_ID, "textures/particle/wisp.png"), false, false))
					.setCullState(new RenderStateShard.CullStateShard(false))
					.setTransparencyState(new RenderStateShard.TransparencyStateShard("lightning_transparency", () -> {
						RenderSystem.enableBlend();
						RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
					}, () -> {
						RenderSystem.disableBlend();
						RenderSystem.defaultBlendFunc();
					})).setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
					.setLightmapState(new RenderStateShard.LightmapStateShard(true)).createCompositeState(true));
	
	public PsionWaveRenderer(Context context) {
		super(context);
	}
	
	@Override
	public void render(PsionWaveEntity entity, float entityYaw, float partialTicks, PoseStack ms,
			MultiBufferSource buffers, int light) {
		SynchedEntityData dm = entity.getEntityData();
		ItemStack colorizer = dm.get(PsionWaveEntity.colorizer);
		int color = RenderHelper.getColorForColorizer(colorizer);
		int r = RenderHelper.r(color);
		int g = RenderHelper.g(color);
		int b = RenderHelper.b(color);
		VertexConsumer buffer = buffers.getBuffer(layer);
		int fullbright = 0xF000F0;
		Quaternion rotation = Vector3f.YP.rotationDegrees(entity.getYRot());
		rotation.mul(Vector3f.XN.rotationDegrees(entity.getXRot()));
		float traveledPercent = (float) (dm.get(PsionWaveEntity.traveled) / dm.get(PsionWaveEntity.distance));
		float size = 4 * traveledPercent * (1 - traveledPercent);
		size += Math.sin(
				dm.get(PsionWaveEntity.frequency) * dm.get(PsionWaveEntity.traveled) / dm.get(PsionWaveEntity.speed))
				* size / 20;
		int particleCount = 90;
		for (float angle = 0; angle < 360; angle += 360f / particleCount) {
			Vector3f pos = new Vector3f(0, size / 2f, 0);
			pos.transform(Vector3f.ZN.rotationDegrees(angle));
			pos.transform(rotation);
			pos.add(0, 0.5f, 0);
			particle(buffer, ms, fullbright, r, g, b, pos, 0.05f);
		}
	}
	
	void particle(VertexConsumer buffer, PoseStack ms, int light, int r, int g, int b, Vector3f pos, float size) {
		float halfSize = size / 2;
		ms.pushPose();
		ms.translate(pos.x(), pos.y(), pos.z());
		ms.mulPose(entityRenderDispatcher.cameraOrientation());
		ms.mulPose(Vector3f.YP.rotationDegrees(180));
		Matrix4f mat = ms.last().pose();
		buffer.vertex(mat, -halfSize, +halfSize, 0).color(r, g, b, 255).uv(0, 1).uv2(light).endVertex();
		buffer.vertex(mat, +halfSize, +halfSize, 0).color(r, g, b, 255).uv(1, 1).uv2(light).endVertex();
		buffer.vertex(mat, +halfSize, -halfSize, 0).color(r, g, b, 255).uv(1, 0).uv2(light).endVertex();
		buffer.vertex(mat, -halfSize, -halfSize, 0).color(r, g, b, 255).uv(0, 0).uv2(light).endVertex();
		ms.popPose();
	}
	
	@Override
	public ResourceLocation getTextureLocation(PsionWaveEntity entity) {
		return null;
	}
	
}
