package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import gdavid.phi.block.MPUBlock;
import gdavid.phi.block.tile.CADHolderTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CADHolderTileRenderer extends TileEntityRenderer<CADHolderTile> {
	
	public CADHolderTileRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}
	
	@Override
	public void render(CADHolderTile holder, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int worldLight,
			int overlay) {
		ms.push();
		ms.translate(0.5f, 1.05f, 0.5f);
		ms.rotate(Vector3f.ZP.rotationDegrees(180));
		ms.rotate(Vector3f.YP.rotationDegrees(holder.getBlockState().get(MPUBlock.HORIZONTAL_FACING).getHorizontalAngle()));
		ms.rotate(Vector3f.XP.rotationDegrees(90));
		ms.scale(0.6f, 0.6f, 0.6f);
		if (holder.hasItem()) {
			Minecraft.getInstance().getItemRenderer().renderItem(holder.item, TransformType.FIXED, worldLight, OverlayTexture.NO_OVERLAY, ms, buf);
		}
		ms.pop();
	}
	
}
