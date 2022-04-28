package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import gdavid.phi.block.CADHolderBlock;
import gdavid.phi.block.tile.CADHolderTile;
import gdavid.phi.block.tile.CADHolderTile.ScanType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class CADHolderTileRenderer extends TileEntityRenderer<CADHolderTile> {
	
	public CADHolderTileRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}
	
	@Override
	@SuppressWarnings("resource")
	public void render(CADHolderTile holder, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int worldLight,
			int overlay) {
		ms.push();
		ms.translate(0.5f, 1.05f, 0.5f);
		ms.rotate(Vector3f.ZP.rotationDegrees(180));
		ms.rotate(Vector3f.YP
				.rotationDegrees(holder.getBlockState().get(CADHolderBlock.HORIZONTAL_FACING).getHorizontalAngle()));
		ms.rotate(Vector3f.XP.rotationDegrees(90));
		if (holder.hasItem()) {
			ms.push();
			ms.scale(0.6f, 0.6f, 0.6f);
			Minecraft.getInstance().getItemRenderer().renderItem(holder.item, TransformType.FIXED, worldLight,
					OverlayTexture.NO_OVERLAY, ms, buf);
			ms.pop();
		}
		if (holder.scan != ScanType.none) {
			float progress = (System.currentTimeMillis() - holder.scanTime) / 2000f;
			if (progress >= 1) {
				holder.scan = ScanType.none;
			} else {
				ms.push();
				ms.rotate(Vector3f.XP.rotationDegrees(180));
				ms.translate(-0.5f, -0.5f, 0.06f);
				ms.scale(1 / 64f, 1 / 64f, 1);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				RenderSystem.color4f(1, 1, 1, 1 - Math.abs(2 * progress - 1));
				Minecraft.getInstance().textureManager.bindTexture(holder.scan.texture);
				AbstractGui.blit(ms, 0, 0, 0, 0, 64, 64, 64, 64);
				RenderSystem.color4f(1, 1, 1, 1);
				RenderSystem.disableBlend();
				ms.pop();
			}
		}
		ms.pop();
	}
	
}
