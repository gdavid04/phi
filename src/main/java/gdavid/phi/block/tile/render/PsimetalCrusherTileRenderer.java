package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import gdavid.phi.Phi;
import gdavid.phi.block.tile.PsimetalCrusherTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PsimetalCrusherTileRenderer extends TileEntityRenderer<PsimetalCrusherTile> {
	
	public static final ResourceLocation modelLoc = new ResourceLocation(Phi.modId, "block/psimetal_crusher_piston");
	
	public PsimetalCrusherTileRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}
	
	@Override
	public void render(PsimetalCrusherTile crusher, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int worldLight, int overlay) {
		IBakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);
		ms.push();
		ms.translate(0, -crusher.getPistonOffset(partialTicks), 0);
		Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(crusher.getWorld(), model, crusher.getBlockState(), crusher.getPos(), ms, buf.getBuffer(RenderType.getSolid()), false, crusher.getWorld().rand, 0, worldLight);
		ms.pop();
	}
	
}
