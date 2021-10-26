package gdavid.phi.spell;

import gdavid.phi.Phi;
import gdavid.phi.spell.operator.BranchOperator;
import gdavid.phi.spell.operator.number.DifferenceOperator;
import gdavid.phi.spell.operator.number.DivModOperator;
import gdavid.phi.spell.operator.number.ExtractDigitOperator;
import gdavid.phi.spell.operator.number.MultiplyAccumulateOperator;
import gdavid.phi.spell.operator.number.SignumPositiveZeroOperator;
import gdavid.phi.spell.operator.number.ToDegreesOperator;
import gdavid.phi.spell.operator.number.ToRadiansOperator;
import gdavid.phi.spell.operator.vector.ClampVectorOperator;
import gdavid.phi.spell.operator.vector.ComponentWiseMultiplyVectorOperator;
import gdavid.phi.spell.operator.vector.NearestAxialVectorOperator;
import gdavid.phi.spell.operator.vector.RoundVectorOperator;
import gdavid.phi.spell.operator.vector.SplitVectorOperator;
import gdavid.phi.spell.operator.vector.TruncateVectorOperator;
import gdavid.phi.spell.operator.vector.raycast.OffsetRaycastOperator;
import gdavid.phi.spell.operator.vector.raycast.PreciseRaycastOperator;
import gdavid.phi.spell.other.BidirectionalConnector;
import gdavid.phi.spell.other.ClockwiseConnector;
import gdavid.phi.spell.other.CounterclockwiseConnector;
import gdavid.phi.spell.other.InOutConnector;
import gdavid.phi.spell.selector.NearbyMarkersSelector;
import gdavid.phi.spell.trick.PsionWaveTrick;
import gdavid.phi.spell.trick.ShadowSequenceTrick;
import gdavid.phi.spell.trick.ShadowTrick;
import gdavid.phi.spell.trick.blink.CasterBlinkTrick;
import gdavid.phi.spell.trick.blink.SwapBlinkTrick;
import gdavid.phi.spell.trick.evaluation.EarlyEvaluateTrick;
import gdavid.phi.spell.trick.evaluation.ReevaluateTrick;
import gdavid.phi.spell.trick.marker.ConjureMarkerTrick;
import gdavid.phi.spell.trick.marker.MoveMarkerTrick;
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
		public static final String condition = prefix + "condition";
		public static final String positive = prefix + "positive";
		public static final String negative = prefix + "negative";
		public static final String div = prefix + "div";
		public static final String mod = prefix + "mod";
		public static final String digit = prefix + "digit";
		public static final String target1 = prefix + "target1";
		public static final String target2 = prefix + "target2";
		
	}
	
	public static class Errors {
		
		public static final String prefix = Phi.modId + ".spellerror.";
		
		public static final String range = prefix + "range";
		public static final String minWave = prefix + "min_wave";
		public static final String errored = prefix + "ed";
		public static final String ambiguous = prefix + "ambiguous";
		public static final String invalidTarget = prefix + "invalid_target";
		
	}
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event) {
		register("trick_psion_wave", PsionWaveTrick.class, Groups.psionManipulation, true);
		register("trick_shadow", ShadowTrick.class, Groups.opticalMagic, true);
		register("trick_shadow_sequence", ShadowSequenceTrick.class, Groups.opticalMagic, false);
		register("trick_conjure_marker", ConjureMarkerTrick.class, Groups.opticalMagic, false);
		register("trick_move_marker", MoveMarkerTrick.class, Groups.opticalMagic, false);
		register("trick_caster_blink", CasterBlinkTrick.class, "movement", false);
		register("trick_swap_blink", SwapBlinkTrick.class, "movement", false);
		
		register("trick_early_evaluate", EarlyEvaluateTrick.class, Groups.dataFlow, true);
		register("trick_reevaluate", ReevaluateTrick.class, Groups.dataFlow, true);
		
		register("selector_nearby_markers", NearbyMarkersSelector.class, Groups.opticalMagic, false);
		
		register("operator_to_degrees", ToDegreesOperator.class, Groups.math, true);
		register("operator_to_radians", ToRadiansOperator.class, Groups.math, false);
		register("operator_multiply_accumulate", MultiplyAccumulateOperator.class, Groups.math, false);
		register("operator_difference", DifferenceOperator.class, Groups.math, false);
		register("operator_div_mod", DivModOperator.class, Groups.math, false);
		register("operator_signum_positive_zero", SignumPositiveZeroOperator.class, Groups.math, false);
		register("operator_extract_digit", ExtractDigitOperator.class, Groups.math, false);
		// register("operator_replace_digit", ReplaceDigitOperator.class, Groups.math,
		// false);
		
		register("operator_vector_component_wise_multiply", ComponentWiseMultiplyVectorOperator.class, Groups.math,
				false);
		register("operator_split_vector", SplitVectorOperator.class, Groups.dataFlow, false);
		register("operator_precise_raycast", PreciseRaycastOperator.class, "block_works", false);
		register("operator_offset_raycast", OffsetRaycastOperator.class, "block_works", false);
		register("operator_nearest_axial_vector", NearestAxialVectorOperator.class, Groups.math, false);
		register("operator_round_vector", RoundVectorOperator.class, Groups.math, false);
		register("operator_truncate_vector", TruncateVectorOperator.class, Groups.math, false);
		register("operator_clamp_vector", ClampVectorOperator.class, Groups.math, false);
		
		register("operator_branch", BranchOperator.class, Groups.dataFlow, false);
		
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
