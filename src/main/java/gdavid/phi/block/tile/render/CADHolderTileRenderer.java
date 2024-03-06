package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import gdavid.phi.block.CADHolderBlock;
import gdavid.phi.block.tile.CADHolderTile;
import gdavid.phi.block.tile.CADHolderTile.ScanType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class CADHolderTileRenderer implements BlockEntityRenderer<CADHolderTile> {
	
	public CADHolderTileRenderer(Context ctx) {}
	
	@Override
	public void render(CADHolderTile holder, float partialTicks, PoseStack ms, MultiBufferSource buf, int worldLight,
			int overlay) {
		ms.pushPose();
		ms.translate(0.5f, 1.05f, 0.5f);
		ms.mulPose(Vector3f.ZP.rotationDegrees(180));
		ms.mulPose(Vector3f.YP
				.rotationDegrees(holder.getBlockState().getValue(CADHolderBlock.FACING).toYRot()));
		ms.mulPose(Vector3f.XP.rotationDegrees(90));
		if (holder.hasItem()) {
			ms.pushPose();
			ms.scale(0.6f, 0.6f, 0.6f);
			Minecraft.getInstance().getItemRenderer().renderStatic(holder.item, TransformType.FIXED, worldLight,
					OverlayTexture.NO_OVERLAY, ms, buf, 0);
			ms.popPose();
		}
		if (holder.scan != ScanType.none) {
			float progress = (System.currentTimeMillis() - holder.scanTime) / 2000f;
			if (progress >= 1) {
				holder.scan = ScanType.none;
			} else {
				ms.pushPose();
				ms.mulPose(Vector3f.XP.rotationDegrees(180));
				ms.translate(-0.5f, -0.5f, 0.06f);
				ms.scale(1 / 64f, 1 / 64f, 1);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				RenderSystem.setShaderColor(1, 1, 1, 1 - Math.abs(2 * progress - 1));
				Minecraft.getInstance().textureManager.bindForSetup(holder.scan.texture);
				GuiComponent.blit(ms, 0, 0, 0, 0, 64, 64, 64, 64);
				RenderSystem.setShaderColor(1, 1, 1, 1);
				RenderSystem.disableBlend();
				ms.popPose();
			}
		}
		ms.popPose();
	}
	
}
