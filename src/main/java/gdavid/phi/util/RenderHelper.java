package gdavid.phi.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
	
	public static int getColorForColorizer(ItemStack colorizer) {
		if (colorizer.getItem() instanceof ICADColorizer) {
			return ((ICADColorizer) colorizer.getItem()).getColor(colorizer);
		}
		return ICADColorizer.DEFAULT_SPELL_COLOR;
	}
	
	public static int r(int color) {
		return (color >> 16) & 0xFF;
	}
	
	public static int g(int color) {
		return (color >> 8) & 0xFF;
	}
	
	public static int b(int color) {
		return color & 0xFF;
	}
	
	public static int a(int color) {
		return (color >> 24) & 0xFF;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void param(MatrixStack ms, IRenderTypeBuffer buffers, int light, int color, SpellParam.Side side) {
		if (!side.isEnabled()) {
			return;
		}
		IVertexBuilder buffer = buffers.getBuffer(PsiAPI.internalHandler.getProgrammerLayer());
		int minX = 4 + side.offx * 9, minY = 4 + side.offy * 9;
		int maxX = minX + 8, maxY = minY + 8;
		float minU = side.u / 256f, minV = side.v / 256f;
		float maxU = minU + 1 / 32f, maxV = minV + 1 / 32f;
		int r = r(color), g = g(color), b = b(color), a = 255;
		Matrix4f mat = ms.getLast().getMatrix();
		buffer.pos(mat, minX, maxY, 0).color(r, g, b, a).tex(minU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, maxX, maxY, 0).color(r, g, b, a).tex(maxU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, maxX, minY, 0).color(r, g, b, a).tex(maxU, minV).lightmap(light).endVertex();
		buffer.pos(mat, minX, minY, 0).color(r, g, b, a).tex(minU, minV).lightmap(light).endVertex();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void doubleParam(MatrixStack ms, IRenderTypeBuffer buffers, int light, int color, SpellParam.Side side) {
		if (!side.isEnabled()) {
			return;
		}
		IVertexBuilder buffer = buffers.getBuffer(PsiAPI.internalHandler.getProgrammerLayer());
		int minX = 4 + side.offx * 9, minY = 4 + side.offy * 9;
		int maxX = minX + 8, maxY = minY + 8;
		float minU = side.u / 256f, minV = side.v / 256f;
		float maxU = minU + 1 / 32f, maxV = minV + 1 / 32f;
		if (side == Side.TOP) {
			minX += 4;
			minU += 1 / 64f;
		} else if (side == Side.BOTTOM) {
			maxX -= 4;
			maxU -= 1 / 64f;
		} else if (side == Side.LEFT) {
			minY += 4;
			minV += 1 / 64f;
		} else if (side == Side.RIGHT) {
			maxY -= 4;
			maxV -= 1 / 64f;
		}
		int r = r(color), g = g(color), b = b(color), a = 255;
		Matrix4f mat = ms.getLast().getMatrix();
		buffer.pos(mat, minX, maxY, 0.05f).color(r, g, b, a).tex(minU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, maxX, maxY, 0.05f).color(r, g, b, a).tex(maxU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, maxX, minY, 0.05f).color(r, g, b, a).tex(maxU, minV).lightmap(light).endVertex();
		buffer.pos(mat, minX, minY, 0.05f).color(r, g, b, a).tex(minU, minV).lightmap(light).endVertex();
	}
	
}
