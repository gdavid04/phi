package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gdavid.phi.block.MPUBlock;
import gdavid.phi.block.tile.MPUTile;
import gdavid.phi.util.RedstoneMode;
import gdavid.phi.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICADColorizer;

import static com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS;

@OnlyIn(Dist.CLIENT)
public class MPUTileRenderer implements BlockEntityRenderer<MPUTile> {
	
	public static final ResourceLocation psiBarTexture = new ResourceLocation(PsiAPI.MOD_ID,
			"textures/gui/psi_bar.png");
	
	public static final int w = 174, h = 184, bw = 24, bh = 132;
	public static final int light = 0xF000F0;
	
	public MPUTileRenderer(Context ctx) {}
	
	@Override
	public void render(MPUTile mpu, float partialTicks, PoseStack ms, MultiBufferSource buf, int worldLight,
			int overlay) {
		ms.pushPose();
		setupTransform(mpu, ms, w + 3 + bw, h);
		drawSpell(mpu, ms, buf, light);
		ms.pushPose();
		ms.translate(w + 3, (h - bh) / 2f, 0);
		drawPsiBar(mpu, ms, buf, light);
		ms.popPose();
		ms.pushPose();
		ms.translate(-7, -26, 0);
		drawRedstoneMode(mpu, ms, buf, light);
		ms.popPose();
		if (mpu.message != null) {
			ms.pushPose();
			// TODO formatting, word wrap, maybe background texture
			Minecraft.getInstance().font.drawInBatch(mpu.message.getString(), 0, h, 0xFFFFFF, false,
					ms.last().pose(), buf, false, 0, light);
			ms.popPose();
		}
		ms.popPose();
	}
	
	public void setupTransform(MPUTile mpu, PoseStack ms, int width, int height) {
		ms.translate(0.5f, 1.62f, 0.5f);
		ms.mulPose(Vector3f.ZP.rotationDegrees(180));
		ms.mulPose(
				Vector3f.YP.rotationDegrees(mpu.getBlockState().getValue(MPUBlock.FACING).toYRot()));
		ms.translate(0, 0, 0.5f);
		ms.mulPose(Vector3f.XP.rotationDegrees(-60));
		ms.scale(1 / 300f, 1 / 300f, -1 / 300f);
		ms.translate(-width / 2f, height / 2f, 0);
	}
	
	public void drawSpell(MPUTile mpu, PoseStack ms, MultiBufferSource buf, int light) {
		Minecraft mc = Minecraft.getInstance();
		try {
			VertexConsumer buffer = buf.getBuffer(
					(RenderType) Class.forName("vazkii.psi.client.gui.GuiProgrammer").getField("LAYER").get(null));
			Matrix4f mat = ms.last().pose();
			buffer.vertex(mat, -7, h - 7, -0.01f).color(255, 255, 255, 128).uv(0, h / 256f).uv2(light).endVertex();
			buffer.vertex(mat, w - 7, h - 7, -0.01f).color(255, 255, 255, 128).uv(w / 256f, h / 256f).uv2(light)
					.endVertex();
			buffer.vertex(mat, w - 7, -7, -0.01f).color(255, 255, 255, 128).uv(w / 256f, 0).uv2(light).endVertex();
			buffer.vertex(mat, -7, -7, -0.01f).color(255, 255, 255, 128).uv(0, 0).uv2(light).endVertex();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mc.font.drawInBatch(I18n.get("psimisc.name"), 0, 164, 0xFFFFFF, false, ms.last().pose(),
				buf, false, 0, light);
		if (mpu.spell != null && !mpu.spell.grid.isEmpty()) {
			mpu.spell.draw(ms, buf, light);
			mc.font.drawInBatch(mpu.spell.name, 38, 164, 0xFFFFFF, false, ms.last().pose(), buf, false,
					0, light);
		}
	}
	
	public void drawPsiBar(MPUTile mpu, PoseStack ms, MultiBufferSource buf, int light) {
		RenderSystem.setShaderTexture(0, psiBarTexture);
		ms.pushPose();
		ms.translate(0, 0, -0.01f);
		RenderSystem.disableCull();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1, 1, 1, 0.5f);
		GuiComponent.blit(ms, 0, 0, 4, 6, bw, bh, 64, 256);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		ms.popPose();
		float percent = mpu.psi / (float) mpu.getPsiCapacity();
		float percent2 = mpu.prevPsi / (float) mpu.getPsiCapacity();
		int color = ICADColorizer.DEFAULT_SPELL_COLOR;
		int r = RenderHelper.r(color);
		int g = RenderHelper.g(color);
		int b = RenderHelper.b(color);
		Matrix4f mat = ms.last().pose();
		BufferBuilder builder = Tesselator.getInstance().getBuilder();
		builder.begin(QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
		builder.vertex(mat, 6, 20 + 106, 0).color(r, g, b, 255).uv(34 / 64f, 106 / 256f).uv2(light).endVertex();
		builder.vertex(mat, 6 + 12, 20 + 106, 0).color(r, g, b, 255).uv(46 / 64f, 106 / 256f).uv2(light).endVertex();
		builder.vertex(mat, 6 + 12, 20 + (1 - percent) * 106, 0).color(r, g, b, 255)
				.uv(46 / 64f, (1 - percent) * 106 / 256f).uv2(light).endVertex();
		builder.vertex(mat, 6, 20 + (1 - percent) * 106, 0).color(r, g, b, 255).uv(34 / 64f, (1 - percent) * 106 / 256f)
				.uv2(light).endVertex();
		builder.vertex(mat, 6, 20 + (1 - percent) * 106, 0).color(r, g, b, 128).uv(34 / 64f, (1 - percent) * 106 / 256f)
				.uv2(light).endVertex();
		builder.vertex(mat, 6 + 12, 20 + (1 - percent) * 106, 0).color(r, g, b, 128)
				.uv(46 / 64f, (1 - percent) * 106 / 256f).uv2(light).endVertex();
		builder.vertex(mat, 6 + 12, 20 + (1 - percent2) * 106, 0).color(r, g, b, 128)
				.uv(46 / 64f, (1 - percent2) * 106 / 256f).uv2(light).endVertex();
		builder.vertex(mat, 6, 20 + (1 - percent2) * 106, 0).color(r, g, b, 128).uv(34 / 64f, (1 - percent2) * 106 / 256f)
				.uv2(light).endVertex();
		Tesselator.getInstance().end();
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
	}
	
	public void drawRedstoneMode(MPUTile mpu, PoseStack ms, MultiBufferSource buf, int light) {
		RenderSystem.setShaderTexture(0, RedstoneMode.texture);
		RenderSystem.disableCull();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1, 1, 1, 0.5f);
		GuiComponent.blit(ms, 0, 0, 0, 0, 16 * mpu.redstoneMode.ordinal(), 16, 64, 32);
		GuiComponent.blit(ms, 16 * mpu.redstoneMode.ordinal(), 0, 16 * mpu.redstoneMode.ordinal(), 16, 16, 16, 64, 32);
		GuiComponent.blit(ms, 16 * (mpu.redstoneMode.ordinal() + 1), 0, 16 * (mpu.redstoneMode.ordinal() + 1), 0,
				64 - 16 * (mpu.redstoneMode.ordinal() + 1), 16, 64, 32);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
	}
	
}
