package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.vertex.PoseStack;
import gdavid.phi.block.TextDisplayBlock;
import gdavid.phi.block.tile.TextDisplayTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextDisplayTileRenderer implements BlockEntityRenderer<TextDisplayTile> {
	
	public static final int light = 0xF000F0;
	
	public TextDisplayTileRenderer(Context ctx) {}
	
	@Override
	public void render(TextDisplayTile display, float partialTicks, PoseStack ms, MultiBufferSource buf,
			int worldLight, int overlay) {
		ms.pushPose();
		ms.translate(0.5f, 0.5f, 0.5f);
		ms.mulPose(Vector3f.YN
				.rotationDegrees(display.getBlockState().getValue(TextDisplayBlock.FACING).toYRot()));
		ms.translate(0, 0, 0.505f);
		ms.scale(1 / 256f, -1 / 256f, 1);
		ms.translate(-80, -80, 0);
		Font font = Minecraft.getInstance().font;
		for (String line : display.text) {
			font.drawInBatch(line, 0, 0, 0xFFFFFF, false, ms.last().pose(), buf, false, 0, light);
			ms.translate(0, 10, 0);
		}
		ms.popPose();
	}
	
}
