package gdavid.phi.block.tile.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import gdavid.phi.block.MPUBlock;
import gdavid.phi.block.tile.MPUTile;
import gdavid.phi.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICADColorizer;

@OnlyIn(Dist.CLIENT)
public class MPUTileRenderer extends TileEntityRenderer<MPUTile> {
	
	public static final ResourceLocation psiBarTexture = new ResourceLocation(PsiAPI.MOD_ID, "textures/gui/psi_bar.png");
	
	public MPUTileRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(MPUTile mpu, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf,
			int worldLight, int overlay) {
		int light = 0xF000F0;
		int w = 174, h = 184, bw = 24, bh = 132;
		Minecraft mc = Minecraft.getInstance();
		ms.push();
		ms.translate(0.5f, 1.62f, 0.5f);
		ms.rotate(Vector3f.ZP.rotationDegrees(180));
		ms.rotate(Vector3f.YP.rotationDegrees(mpu.getBlockState().get(MPUBlock.HORIZONTAL_FACING).getHorizontalAngle()));
		ms.translate(0, 0, 0.5f);
		ms.rotate(Vector3f.XP.rotationDegrees(-60));
		ms.scale(1/300f, 1/300f, -1/300f);
		ms.translate(-(w + 3 + bw)/2, h/2, 0);
		if (mpu.spell != null && !mpu.spell.grid.isEmpty()) {
			mpu.spell.draw(ms, buf, light);
			try {
				IVertexBuilder buffer = buf.getBuffer((RenderType) Class.forName("vazkii.psi.client.gui.GuiProgrammer").getField("LAYER").get(null));
				Matrix4f mat = ms.getLast().getMatrix();
				buffer.pos(mat, -7, h-7, -0.01f).color(255, 255, 255, 128).tex(0, h / 256f).lightmap(light).endVertex();
				buffer.pos(mat, w-7, h-7, -0.01f).color(255, 255, 255, 128).tex(w / 256f, h / 256f).lightmap(light).endVertex();
				buffer.pos(mat, w-7, -7, -0.01f).color(255, 255, 255, 128).tex(w / 256f, 0).lightmap(light).endVertex();
				buffer.pos(mat, -7, -7, -0.01f).color(255, 255, 255, 128).tex(0, 0).lightmap(light).endVertex();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mc.fontRenderer.renderString(I18n.format("psimisc.name"), 0, 164, 0xFFFFFF, false, ms.getLast().getMatrix(), buf, false, 0, light);
			mc.fontRenderer.renderString(mpu.spell.name, 38, 164, 0xFFFFFF, false, ms.getLast().getMatrix(), buf, false, 0, light);
		}
		mc.textureManager.bindTexture(psiBarTexture);
		ms.translate(w + 3, (h - bh) / 2f, 0);
		ms.push();
		ms.translate(0, 0, -0.01f);
		RenderSystem.disableCull();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1, 1, 1, 0.5f);
		AbstractGui.blit(ms, 0, 0, 4, 6, bw, bh, 64, 256);
		RenderSystem.color4f(1, 1, 1, 1);
		ms.pop();
		float percent = mpu.psi / (float) mpu.getPsiCapacity();
		float percent2 = mpu.prevPsi / (float) mpu.getPsiCapacity();
		int color = ICADColorizer.DEFAULT_SPELL_COLOR;
		int r = RenderHelper.r(color);
		int g = RenderHelper.g(color);
		int b = RenderHelper.b(color);
		Matrix4f mat = ms.getLast().getMatrix();
		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		builder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
		builder.pos(mat, 6, 20 + 106, 0).color(r, g, b, 255).tex(34/64f, 106/256f).lightmap(light).endVertex();
		builder.pos(mat, 6 + 12, 20 + 106, 0).color(r, g, b, 255).tex(46/64f, 106/256f).lightmap(light).endVertex();
		builder.pos(mat, 6 + 12, 20 + (1 - percent) * 106, 0).color(r, g, b, 255).tex(46/64f, (1 - percent) * 106/256f).lightmap(light).endVertex();
		builder.pos(mat, 6, 20 + (1 - percent) * 106, 0).color(r, g, b, 255).tex(34/64f, (1 - percent) * 106/256f).lightmap(light).endVertex();
		builder.pos(mat, 6, 20 + (1 - percent) * 106, 0).color(r, g, b, 128).tex(34/64f, (1 - percent) * 106/256f).lightmap(light).endVertex();
		builder.pos(mat, 6 + 12, 20 + (1 - percent) * 106, 0).color(r, g, b, 128).tex(46/64f, (1 - percent) * 106/256f).lightmap(light).endVertex();
		builder.pos(mat, 6 + 12, 20 + (1 - percent2) * 106, 0).color(r, g, b, 128).tex(46/64f, (1 - percent2) * 106/256f).lightmap(light).endVertex();
		builder.pos(mat, 6, 20 + (1 - percent2) * 106, 0).color(r, g, b, 128).tex(34/64f, (1 - percent2) * 106/256f).lightmap(light).endVertex();
		Tessellator.getInstance().draw();
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
		ms.pop();
	}
	
}
