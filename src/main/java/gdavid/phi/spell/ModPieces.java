package gdavid.phi.spell;

import gdavid.phi.Phi;
import gdavid.phi.spell.operator.number.MultiplyAccumulateOperator;
import gdavid.phi.spell.operator.number.ToDegreesOperator;
import gdavid.phi.spell.operator.number.ToRadiansOperator;
import gdavid.phi.spell.operator.vector.ComponentWiseMultiplyVectorOperator;
import gdavid.phi.spell.operator.vector.SplitVectorOperator;
import gdavid.phi.spell.other.BidirectionalConnector;
import gdavid.phi.spell.other.ClockwiseConnector;
import gdavid.phi.spell.other.CounterclockwiseConnector;
import gdavid.phi.spell.other.InOutConnector;
import gdavid.phi.spell.trick.PsionWaveTrick;
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
		public static final String math = "math";
		
	}
	
	public static class Params {
		
		public static final String prefix = Phi.modId + ".spellparam.";
		
		public static final String speed = PsiAPI.MOD_ID + ".spellparam.speed";
		public static final String frequency = prefix + "frequency";
		public static final String from = prefix + "from";
		public static final String to = prefix + "to";
		public static final String fromTo = prefix + "from_to";
		public static final String toFrom = prefix + "to_from";
		
	}
	
	public static class Errors {
		
		public static final String prefix = Phi.modId + ".spellerror.";
		
		public static final String range = prefix + "range";
		
	}
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event) {
		register("trick_psion_wave", PsionWaveTrick.class, Groups.psionManipulation, true);
		register("trick_shadow", ShadowTrick.class, Groups.opticalMagic, true);
		register("trick_shadow_sequence", ShadowSequenceTrick.class, Groups.opticalMagic, false);
		
		register("operator_to_degrees", ToDegreesOperator.class, Groups.math, true);
		register("operator_to_radians", ToRadiansOperator.class, Groups.math, true);
		register("operator_multiply_accumulate", MultiplyAccumulateOperator.class, Groups.math, true);
		
		register("operator_vector_component_wise_multiply", ComponentWiseMultiplyVectorOperator.class, Groups.math,
				true);
		register("operator_split_vector", SplitVectorOperator.class, Groups.dataFlow, true);
		
		register("connector_clockwise", ClockwiseConnector.class, Groups.dataFlow, false);
		register("connector_counterclockwise", CounterclockwiseConnector.class, Groups.dataFlow, false);
		register("connector_bidirectional", BidirectionalConnector.class, Groups.dataFlow, false);
		register("connector_in_out", InOutConnector.class, Groups.dataFlow, false);
	}
	
	public static void register(String id, Class<? extends SpellPiece> piece, String group, boolean main) {
		PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(Phi.modId, id), piece);
		PsiAPI.addPieceToGroup(piece, new ResourceLocation(Phi.modId, group), main);
	}
	
}
