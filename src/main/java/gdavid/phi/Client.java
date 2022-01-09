package gdavid.phi;

import gdavid.phi.block.tile.MPUTile;
import gdavid.phi.block.tile.render.MPUTileRenderer;
import gdavid.phi.entity.MarkerEntity;
import gdavid.phi.entity.PsiProjectileEntity;
import gdavid.phi.entity.PsionWaveEntity;
import gdavid.phi.entity.render.MarkerRenderer;
import gdavid.phi.entity.render.PsiProjectileRenderer;
import gdavid.phi.entity.render.PsionWaveRenderer;
import gdavid.phi.spell.connector.BidirectionalConnector;
import gdavid.phi.spell.operator.number.DivModOperator;
import gdavid.phi.spell.operator.text.SplitTextAtOperator;
import gdavid.phi.spell.operator.text.SplitTextOperator;
import gdavid.phi.spell.operator.vector.SplitVectorOperator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vazkii.psi.api.ClientPsiAPI;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class Client {
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(PsionWaveEntity.type, PsionWaveRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(PsiProjectileEntity.type, PsiProjectileRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(MarkerEntity.type, MarkerRenderer::new);
		
		ClientRegistry.bindTileEntityRenderer(MPUTile.type, MPUTileRenderer::new);
	}
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event) {
		ClientPsiAPI.registerPieceTexture(new ResourceLocation(Phi.modId, "operator_div_mod_lines"),
				DivModOperator.lineTexture);
		ClientPsiAPI.registerPieceTexture(new ResourceLocation(Phi.modId, "operator_split_vector_lines"),
				SplitVectorOperator.lineTexture);
		ClientPsiAPI.registerPieceTexture(new ResourceLocation(Phi.modId, "operator_split_text_lines"),
				SplitTextOperator.lineTexture);
		ClientPsiAPI.registerPieceTexture(new ResourceLocation(Phi.modId, "operator_split_text_at_lines"),
				SplitTextAtOperator.lineTexture);
		ClientPsiAPI.registerPieceTexture(new ResourceLocation(Phi.modId, "connector_bidirectional_lines"),
				BidirectionalConnector.lineTexture);
	}
	
}
