package gdavid.phi.spell;

import gdavid.phi.Phi;
import gdavid.phi.spell.connector.BidirectionalConnector;
import gdavid.phi.spell.connector.BridgeConnector;
import gdavid.phi.spell.connector.ClockwiseConnector;
import gdavid.phi.spell.connector.CounterclockwiseConnector;
import gdavid.phi.spell.connector.InOutConnector;
import gdavid.phi.spell.constant.TextConstant;
import gdavid.phi.spell.operator.BranchOperator;
import gdavid.phi.spell.operator.entity.EntityEyePositionOperator;
import gdavid.phi.spell.operator.entity.EntityFootPositionOperator;
import gdavid.phi.spell.operator.entity.EntityNameOperator;
import gdavid.phi.spell.operator.entity.EntitySneakStatusOperator;
import gdavid.phi.spell.operator.number.DifferenceOperator;
import gdavid.phi.spell.operator.number.DivModOperator;
import gdavid.phi.spell.operator.number.ExtractDigitOperator;
import gdavid.phi.spell.operator.number.MultiplyAccumulateOperator;
import gdavid.phi.spell.operator.number.NumberFromTextOperator;
import gdavid.phi.spell.operator.number.SignumNegativeZeroOperator;
import gdavid.phi.spell.operator.number.SignumPositiveZeroOperator;
import gdavid.phi.spell.operator.number.ToDegreesOperator;
import gdavid.phi.spell.operator.number.ToRadiansOperator;
import gdavid.phi.spell.operator.text.AppendTextOperator;
import gdavid.phi.spell.operator.text.AsTextOperator;
import gdavid.phi.spell.operator.text.CharacterCodeAtOperator;
import gdavid.phi.spell.operator.text.CharacterFromCodeOperator;
import gdavid.phi.spell.operator.text.LowerCaseOperator;
import gdavid.phi.spell.operator.text.SplitTextAtOperator;
import gdavid.phi.spell.operator.text.SplitTextOperator;
import gdavid.phi.spell.operator.text.TextLengthOperator;
import gdavid.phi.spell.operator.text.UpperCaseOperator;
import gdavid.phi.spell.operator.vector.ClampVectorOperator;
import gdavid.phi.spell.operator.vector.ComponentWiseMultiplyVectorOperator;
import gdavid.phi.spell.operator.vector.NearestAxialVectorOperator;
import gdavid.phi.spell.operator.vector.ReplaceVectorComponentOperator;
import gdavid.phi.spell.operator.vector.RoundVectorOperator;
import gdavid.phi.spell.operator.vector.SplitVectorOperator;
import gdavid.phi.spell.operator.vector.TruncateVectorOperator;
import gdavid.phi.spell.operator.vector.raycast.FocusedBlockFaceOperator;
import gdavid.phi.spell.operator.vector.raycast.FocusedBlockOperator;
import gdavid.phi.spell.operator.vector.raycast.OffsetRaycastOperator;
import gdavid.phi.spell.operator.vector.raycast.PreciseRaycastOperator;
import gdavid.phi.spell.selector.CasterSpeechSelector;
import gdavid.phi.spell.selector.NearbyMarkersSelector;
import gdavid.phi.spell.selector.SavedVectorComponentSelector;
import gdavid.phi.spell.selector.SpellNameSelector;
import gdavid.phi.spell.selector.mpu.ReadVectorStorageSelector;
import gdavid.phi.spell.trick.PsionWaveTrick;
import gdavid.phi.spell.trick.SaveVectorComponentTrick;
import gdavid.phi.spell.trick.ShadowSequenceTrick;
import gdavid.phi.spell.trick.ShadowTrick;
import gdavid.phi.spell.trick.blink.CasterBlinkTrick;
import gdavid.phi.spell.trick.blink.SwapBlinkTrick;
import gdavid.phi.spell.trick.evaluation.EarlyEvaluateTrick;
import gdavid.phi.spell.trick.evaluation.ReevaluateTrick;
import gdavid.phi.spell.trick.marker.ConjureMarkerTrick;
import gdavid.phi.spell.trick.marker.MoveMarkerTrick;
import gdavid.phi.spell.trick.mpu.PsiTransferTrick;
import gdavid.phi.spell.trick.mpu.SetComparatorOutputTrick;
import gdavid.phi.spell.trick.mpu.SetTimeTrick;
import gdavid.phi.spell.trick.mpu.WriteVectorStorageTrick;
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
		public static final String text = "text";
		
	}
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event) {
		register("trick_psion_wave", PsionWaveTrick.class, Groups.psionManipulation, true);
		register("trick_psi_transfer", PsiTransferTrick.class, Groups.psionManipulation, false);
		register("trick_shadow", ShadowTrick.class, Groups.opticalMagic, true);
		register("trick_shadow_sequence", ShadowSequenceTrick.class, Groups.opticalMagic, false);
		register("trick_conjure_marker", ConjureMarkerTrick.class, Groups.opticalMagic, false);
		register("trick_move_marker", MoveMarkerTrick.class, Groups.opticalMagic, false);
		register("trick_caster_blink", CasterBlinkTrick.class, "movement", false);
		register("trick_swap_blink", SwapBlinkTrick.class, "movement", false);
		
		register("trick_early_evaluate", EarlyEvaluateTrick.class, Groups.dataFlow, true);
		register("trick_reevaluate", ReevaluateTrick.class, Groups.dataFlow, true);
		
		register("trick_save_vector_component", SaveVectorComponentTrick.class, Groups.dataFlow, false);
		register("trick_write_vector_storage", WriteVectorStorageTrick.class, Groups.dataFlow, false);
		register("trick_set_comparator_output", SetComparatorOutputTrick.class, Groups.dataFlow, false);
		register("trick_set_time", SetTimeTrick.class, Groups.dataFlow, false);
		
		register("selector_nearby_markers", NearbyMarkersSelector.class, Groups.opticalMagic, false);
		
		register("selector_saved_vector_component", SavedVectorComponentSelector.class, Groups.dataFlow, false);
		register("selector_read_vector_storage", ReadVectorStorageSelector.class, Groups.dataFlow, false);

		register("selector_spell_name", SpellNameSelector.class, Groups.text, false);
		register("selector_caster_speech", CasterSpeechSelector.class, Groups.text, false);
		
		register("operator_to_degrees", ToDegreesOperator.class, Groups.math, true);
		register("operator_to_radians", ToRadiansOperator.class, Groups.math, false);
		register("operator_multiply_accumulate", MultiplyAccumulateOperator.class, Groups.math, false);
		register("operator_difference", DifferenceOperator.class, Groups.math, false);
		register("operator_div_mod", DivModOperator.class, Groups.math, false);
		register("operator_signum_positive_zero", SignumPositiveZeroOperator.class, Groups.math, false);
		register("operator_signum_negative_zero", SignumNegativeZeroOperator.class, Groups.math, false);
		register("operator_extract_digit", ExtractDigitOperator.class, Groups.math, false);
		// register("operator_replace_digit", ReplaceDigitOperator.class, Groups.math,
		// false);
		
		register("operator_vector_component_wise_multiply", ComponentWiseMultiplyVectorOperator.class, Groups.math,
				false);
		register("operator_split_vector", SplitVectorOperator.class, Groups.dataFlow, false);
		register("operator_replace_vector_component", ReplaceVectorComponentOperator.class, Groups.dataFlow, false);
		register("operator_precise_raycast", PreciseRaycastOperator.class, "block_works", false);
		register("operator_offset_raycast", OffsetRaycastOperator.class, "block_works", false);
		register("operator_nearest_axial_vector", NearestAxialVectorOperator.class, Groups.math, false);
		register("operator_round_vector", RoundVectorOperator.class, Groups.math, false);
		register("operator_truncate_vector", TruncateVectorOperator.class, Groups.math, false);
		register("operator_clamp_vector", ClampVectorOperator.class, Groups.math, false);
		register("operator_focused_block", FocusedBlockOperator.class, "entities_intro", false);
		register("operator_focused_block_face", FocusedBlockFaceOperator.class, "entities_intro", false);
		
		register("operator_entity_sneak_status", EntitySneakStatusOperator.class, "entities_intro", false);
		register("operator_entity_name", EntityNameOperator.class, "entities_intro", false);
		register("operator_entity_eye_position", EntityEyePositionOperator.class, "entities_intro", false);
		register("operator_entity_foot_position", EntityFootPositionOperator.class, "entities_intro", false);
		
		register("operator_as_text", AsTextOperator.class, Groups.text, false);
		register("operator_text_length", TextLengthOperator.class, Groups.text, false);
		register("operator_append_text", AppendTextOperator.class, Groups.text, false);
		register("operator_character_code_at", CharacterCodeAtOperator.class, Groups.text, false);
		register("operator_character_from_code", CharacterFromCodeOperator.class, Groups.text, false);
		register("operator_lower_case", LowerCaseOperator.class, Groups.text, false);
		register("operator_upper_case", UpperCaseOperator.class, Groups.text, false);
		register("operator_split_text", SplitTextOperator.class, Groups.text, false);
		register("operator_split_text_at", SplitTextAtOperator.class, Groups.text, false);
		register("operator_number_from_text", NumberFromTextOperator.class, Groups.text, false);
		
		register("operator_branch", BranchOperator.class, Groups.dataFlow, false);
		
		register("connector_clockwise", ClockwiseConnector.class, Groups.dataFlow, false);
		register("connector_counterclockwise", CounterclockwiseConnector.class, Groups.dataFlow, false);
		register("connector_bidirectional", BidirectionalConnector.class, Groups.dataFlow, false);
		register("connector_in_out", InOutConnector.class, Groups.dataFlow, false);
		register("connector_bridge", BridgeConnector.class, Groups.dataFlow, false);
		
		register("constant_text", TextConstant.class, Groups.text, true);
	}
	
	public static void register(String id, Class<? extends SpellPiece> piece, String group, boolean main) {
		PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(Phi.modId, id), piece);
		PsiAPI.addPieceToGroup(piece, new ResourceLocation(Phi.modId, group), main);
	}
	
}
