package gdavid.phi.entity.render.form;

import com.mojang.blaze3d.matrix.MatrixStack;
import gdavid.phi.entity.form.CircleFormEntity;
import gdavid.phi.util.RenderHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.client.render.entity.RenderSpellCircle;

@OnlyIn(Dist.CLIENT)
public class CircleFormRenderer extends EntityRenderer<CircleFormEntity> {
	
	public CircleFormRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void render(CircleFormEntity entity, float entityYaw, float partialTicks, MatrixStack ms,
			IRenderTypeBuffer buffers, int light) {
		EntityDataManager dm = entity.getDataManager();
		ItemStack colorizer = dm.get(CircleFormEntity.colorizer);
		int color = RenderHelper.getColorForColorizer(colorizer);
		float time = entity.ticksExisted + partialTicks;
		float scale = Math.min(1, Math.min(time, 110 - time) / 5);
		RenderSpellCircle.renderSpellCircle(time, scale, 1, 0, 1, 0, color, ms, buffers);
	}
	
	@Override
	public ResourceLocation getEntityTexture(CircleFormEntity entity) {
		return null;
	}
	
}
