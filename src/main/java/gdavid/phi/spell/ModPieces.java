package gdavid.phi.spell;

import gdavid.phi.Phi;
import gdavid.phi.spell.operator.SplitVectorOperator;
import gdavid.phi.spell.other.ClockwiseConnector;
import gdavid.phi.spell.other.CounterclockwiseConnector;
import gdavid.phi.spell.trick.PsionWaveTrick;
import gdavid.phi.spell.trick.RewriteVariableTrick;
import gdavid.phi.spell.trick.ShadowSequenceTrick;
import gdavid.phi.spell.trick.ShadowTrick;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.SpellPiece;

@EventBusSubscriber(bus = Bus.MOD)
public class ModPieces {
	
	public static class Groups {
		
		public static final String psionManipulation = "psion_manipulation";
		public static final String opticalMagic = "opticl_magic";
		public static final String dataFlow = "data_flow";
		public static final String variables = "variables";
		
	}
	
	public static class Params {
		
		public static final String speed = PsiAPI.MOD_ID + ".spellparam.speed";
		public static final String frequency = Phi.modId + ".spellparam.frequency";
		
	}
	
	public static class Errors {
		
		public static final String range = Phi.modId + ".spellerror.range";
		
	}
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event) {
		register("trick_psion_wave", PsionWaveTrick.class, Groups.psionManipulation, true);
		register("trick_shadow", ShadowTrick.class, Groups.opticalMagic, true);
		register("trick_shadow_sequence", ShadowSequenceTrick.class, Groups.opticalMagic, true);
		register("connector_clockwise", ClockwiseConnector.class, Groups.dataFlow, true);
		register("connector_counterclockwise", CounterclockwiseConnector.class, Groups.dataFlow, true);
		register("operator_split_vector", SplitVectorOperator.class, Groups.dataFlow, true);
		register("trick_rewrite_variable", RewriteVariableTrick.class, Groups.variables, true);
	}
	
	public static void register(String id, Class<? extends SpellPiece> piece, String group, boolean main) {
		PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(Phi.modId, id), piece);
		PsiAPI.addPieceToGroup(piece, new ResourceLocation(Phi.modId, group), main);
	}
	
}
