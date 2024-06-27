package gdavid.phi;

import gdavid.phi.block.tile.*;
import gdavid.phi.block.tile.render.*;
import gdavid.phi.entity.MarkerEntity;
import gdavid.phi.entity.PsiProjectileEntity;
import gdavid.phi.entity.PsionWaveEntity;
import gdavid.phi.entity.SpiritEntity;
import gdavid.phi.entity.render.MarkerRenderer;
import gdavid.phi.entity.render.PsiProjectileRenderer;
import gdavid.phi.entity.render.PsionWaveRenderer;
import gdavid.phi.entity.render.SpiritRenderer;
import gdavid.phi.spell.connector.BidirectionalConnector;
import gdavid.phi.spell.connector.BridgeConnector;
import gdavid.phi.spell.connector.InOutConnector;
import gdavid.phi.spell.operator.number.DivModOperator;
import gdavid.phi.spell.operator.text.SplitTextAtOperator;
import gdavid.phi.spell.operator.text.SplitTextOperator;
import gdavid.phi.spell.operator.vector.SplitVectorOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.RegisterEvent;
import vazkii.psi.api.ClientPsiAPI;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class Client {
	
	@SubscribeEvent
	public static void clientSetup(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(PsionWaveEntity.type, PsionWaveRenderer::new);
		event.registerEntityRenderer(PsiProjectileEntity.type, PsiProjectileRenderer::new);
		event.registerEntityRenderer(MarkerEntity.type, MarkerRenderer::new);
		event.registerEntityRenderer(SpiritEntity.type, SpiritRenderer::new);
		
		event.registerBlockEntityRenderer(MPUTile.type, MPUTileRenderer::new);
		event.registerBlockEntityRenderer(CADHolderTile.type, CADHolderTileRenderer::new);
		event.registerBlockEntityRenderer(TextDisplayTile.type, TextDisplayTileRenderer::new);
		event.registerBlockEntityRenderer(SpellDisplayTile.type, SpellDisplayTileRenderer::new);
		event.registerBlockEntityRenderer(PsimetalCrusherTile.type, PsimetalCrusherTileRenderer::new);
	}
	
	@SubscribeEvent
	public static void init(RegisterEvent event) {
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
		ClientPsiAPI.registerPieceTexture(new ResourceLocation(Phi.modId, "connector_bidirectional_hint"),
				BidirectionalConnector.hintTexture);
		ClientPsiAPI.registerPieceTexture(new ResourceLocation(Phi.modId, "connector_bridge_lines"),
				BridgeConnector.lineTexture);
		ClientPsiAPI.registerPieceTexture(new ResourceLocation(Phi.modId, "connector_in_out_hint"),
				InOutConnector.hintTexture);
	}
	
	@SubscribeEvent
	public static void registerModels(ModelEvent.RegisterAdditional event) {
		event.register(PsimetalCrusherTileRenderer.modelLoc);
	}
	
}
