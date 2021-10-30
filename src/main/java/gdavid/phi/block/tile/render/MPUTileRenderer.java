package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import gdavid.phi.block.MPUBlock;
import gdavid.phi.block.tile.MPUTile;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MPUTileRenderer extends TileEntityRenderer<MPUTile> {
	
	public MPUTileRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}
	
	@Override
	public void render(MPUTile mpu, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf,
			int worldLight, int overlay) {
		ms.push();
		int light = 0xF000F0;
		ms.translate(0.5f, 1.62f, 0.5f);
		ms.rotate(Vector3f.ZP.rotationDegrees(180));
		ms.rotate(Vector3f.YP.rotationDegrees(mpu.getBlockState().get(MPUBlock.HORIZONTAL_FACING).getHorizontalAngle()));
		ms.translate(-0.5f, 0, 0.5f);
		ms.translate(0.095f, 0, -0.19f);
		ms.rotate(Vector3f.XP.rotationDegrees(-60));
		ms.scale(1/200f, 1/200f, -1/300f);
		if (mpu.spell != null) {
			mpu.spell.draw(ms, buf, light);
			// TODO highlight next piece
		}
		// TODO draw ui texture
		ms.pop();
	}
	
}
