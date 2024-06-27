package gdavid.phi.block.tile.render;

import com.mojang.blaze3d.vertex.PoseStack;
import gdavid.phi.Phi;
import gdavid.phi.block.tile.PsimetalCrusherTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PsimetalCrusherTileRenderer implements BlockEntityRenderer<PsimetalCrusherTile> {
	
	public static final ResourceLocation modelLoc = new ResourceLocation(Phi.modId, "block/psimetal_crusher_piston");
	
	public PsimetalCrusherTileRenderer(Context ctx) {}
	
	@Override
	public void render(PsimetalCrusherTile crusher, float partialTicks, PoseStack ms, MultiBufferSource buf, int worldLight, int overlay) {
		BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);
		ms.pushPose();
		ms.translate(0, -crusher.getPistonOffset(partialTicks), 0);
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(crusher.getLevel(), model, crusher.getBlockState(), crusher.getBlockPos(), ms, buf.getBuffer(RenderType.solid()), false, crusher.getLevel().random, 0, worldLight);
		ms.popPose();
	}
	
}
