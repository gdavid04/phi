package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import gdavid.phi.block.InfusionLaserBlock;
import gdavid.phi.block.tile.SpellDisplayTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpellDisplayTileRenderer implements BlockEntityRenderer<SpellDisplayTile> {
	
	public static final int w = 174, h = 184;
	public static final int light = 0xF000F0;
	
	public SpellDisplayTileRenderer(Context ctx) {}
	
	@Override
	public void render(SpellDisplayTile tile, float partialTicks, PoseStack ms, MultiBufferSource buf, int worldLight,
			int overlay) {
		ms.pushPose();
		ms.translate(0.5f, 0.5f, 0.5f);
		Direction dir = tile.getBlockState().getValue(InfusionLaserBlock.FACING);
		if (dir.getAxis() == Axis.Y) {
			Quaternion look = Minecraft.getInstance().getBlockEntityRenderDispatcher().camera.rotation();
			Quaternion faceCamera = new Quaternion(0, look.j(), 0, look.r());
			faceCamera.normalize();
			ms.mulPose(faceCamera);
			ms.mulPose(Vector3f.YP.rotationDegrees(180));
		}
		ms.mulPose(dir.getRotation());
		ms.translate(0, 0.4f, 0);
		ms.mulPose(Vector3f.XP.rotationDegrees(90));
		ms.scale(1.2f / 300f, 1.2f / 300f, -1.2f / 300f);
		ms.translate(-w / 2f, -h / 2f, 0);
		drawSpell(tile, ms, buf, light);
		ms.popPose();
	}
	
	public void drawSpell(SpellDisplayTile tile, PoseStack ms, MultiBufferSource buf, int light) {
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
		if (tile.spell != null && !tile.spell.grid.isEmpty()) {
			tile.spell.draw(ms, buf, light);
			mc.font.drawInBatch(tile.spell.name, 38, 164, 0xFFFFFF, false, ms.last().pose(), buf, false,
					0, light);
		}
	}
	
}
