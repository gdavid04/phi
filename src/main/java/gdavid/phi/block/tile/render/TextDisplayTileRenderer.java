package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import gdavid.phi.block.TextDisplayBlock;
import gdavid.phi.block.tile.TextDisplayTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextDisplayTileRenderer extends TileEntityRenderer<TextDisplayTile> {
	
	public static final int light = 0xF000F0;
	
	public TextDisplayTileRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}
	
	@Override
	@SuppressWarnings("resource")
	public void render(TextDisplayTile display, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf,
			int worldLight, int overlay) {
		ms.push();
		ms.translate(0.5f, 0.5f, 0.5f);
		ms.rotate(Vector3f.YN
				.rotationDegrees(display.getBlockState().get(TextDisplayBlock.HORIZONTAL_FACING).getHorizontalAngle()));
		ms.translate(0, 0, 0.505f);
		ms.scale(1 / 256f, -1 / 256f, 1);
		ms.translate(-80, -80, 0);
		FontRenderer font = Minecraft.getInstance().fontRenderer;
		for (String line : display.text) {
			font.renderString(line, 0, 0, 0xFFFFFF, false, ms.getLast().getMatrix(), buf, false, 0, light);
			ms.translate(0, 10, 0);
		}
		ms.pop();
	}
	
}
