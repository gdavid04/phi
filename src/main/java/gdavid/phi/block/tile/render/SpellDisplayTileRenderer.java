package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import gdavid.phi.block.InfusionLaserBlock;
import gdavid.phi.block.tile.SpellDisplayTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpellDisplayTileRenderer extends TileEntityRenderer<SpellDisplayTile> {
	
	public static final int w = 174, h = 184;
	public static final int light = 0xF000F0;
	
	public SpellDisplayTileRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}
	
	@Override
	public void render(SpellDisplayTile tile, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int worldLight,
			int overlay) {
		ms.push();
		ms.translate(0.5f, 0.5f, 0.5f);
		Direction dir = tile.getBlockState().get(InfusionLaserBlock.FACING);
		if (dir.getAxis() == Axis.Y) {
			Quaternion look = renderDispatcher.renderInfo.getRotation();
			Quaternion faceCamera = new Quaternion(0, look.getY(), 0, look.getW());
			faceCamera.normalize();
			ms.rotate(faceCamera);
			ms.rotate(Vector3f.YP.rotationDegrees(180));
		}
		ms.rotate(dir.getRotation());
		ms.translate(0, 0.4f, 0);
		ms.rotate(Vector3f.XP.rotationDegrees(90));
		ms.scale(1.2f / 300f, 1.2f / 300f, -1.2f / 300f);
		ms.translate(-w / 2f, -h / 2f, 0);
		drawSpell(tile, ms, buf, light);
		ms.pop();
	}
	
	public void drawSpell(SpellDisplayTile tile, MatrixStack ms, IRenderTypeBuffer buf, int light) {
		Minecraft mc = Minecraft.getInstance();
		try {
			IVertexBuilder buffer = buf.getBuffer(
					(RenderType) Class.forName("vazkii.psi.client.gui.GuiProgrammer").getField("LAYER").get(null));
			Matrix4f mat = ms.getLast().getMatrix();
			buffer.pos(mat, -7, h - 7, -0.01f).color(255, 255, 255, 128).tex(0, h / 256f).lightmap(light).endVertex();
			buffer.pos(mat, w - 7, h - 7, -0.01f).color(255, 255, 255, 128).tex(w / 256f, h / 256f).lightmap(light)
					.endVertex();
			buffer.pos(mat, w - 7, -7, -0.01f).color(255, 255, 255, 128).tex(w / 256f, 0).lightmap(light).endVertex();
			buffer.pos(mat, -7, -7, -0.01f).color(255, 255, 255, 128).tex(0, 0).lightmap(light).endVertex();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mc.fontRenderer.renderString(I18n.format("psimisc.name"), 0, 164, 0xFFFFFF, false, ms.getLast().getMatrix(),
				buf, false, 0, light);
		if (tile.spell != null && !tile.spell.grid.isEmpty()) {
			tile.spell.draw(ms, buf, light);
			mc.fontRenderer.renderString(tile.spell.name, 38, 164, 0xFFFFFF, false, ms.getLast().getMatrix(), buf, false,
					0, light);
		}
	}
	
}
