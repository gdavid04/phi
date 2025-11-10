package gdavid.phi.spell;

import gdavid.phi.Phi;
import gdavid.phi.spell.connector.*;
import gdavid.phi.spell.constant.*;
import gdavid.phi.spell.operator.*;
import gdavid.phi.spell.operator.entity.*;
import gdavid.phi.spell.operator.error.*;
import gdavid.phi.spell.operator.number.*;
import gdavid.phi.spell.operator.text.*;
import gdavid.phi.spell.operator.vector.*;
import gdavid.phi.spell.operator.vector.raycast.*;
import gdavid.phi.spell.selector.*;
import gdavid.phi.spell.selector.cable.*;
import gdavid.phi.spell.selector.mpu.*;
import gdavid.phi.spell.selector.spirit.*;
import gdavid.phi.spell.trick.*;
import gdavid.phi.spell.trick.acceleration.*;
import gdavid.phi.spell.trick.blink.*;
import gdavid.phi.spell.trick.cable.*;
import gdavid.phi.spell.trick.evaluation.*;
import gdavid.phi.spell.trick.marker.*;
import gdavid.phi.spell.trick.mpu.*;
import gdavid.phi.spell.trick.spirit.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.common.lib.LibPieceGroups;

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
	public static void init(RegisterEvent event) {
		event.register(Keys.ITEMS, handler -> {
			register("trick_psion_wave", PsionWaveTrick.class, Groups.psionManipulation, true);
			register("trick_psi_transfer", PsiTransferTrick.class, Groups.psionManipulation);
			register("trick_shadow", ShadowTrick.class, Groups.opticalMagic, true);
			register("trick_shadow_sequence", ShadowSequenceTrick.class, Groups.opticalMagic);
			register("trick_conjure_marker", ConjureMarkerTrick.class, Groups.opticalMagic);
			register("trick_move_marker", MoveMarkerTrick.class, Groups.opticalMagic);
			register("trick_caster_blink", CasterBlinkTrick.class, LibPieceGroups.MOVEMENT);
			register("trick_swap_blink", SwapBlinkTrick.class, LibPieceGroups.MOVEMENT);
			register("trick_swap_blink_position", SwapBlinkPositionTrick.class, LibPieceGroups.MOVEMENT);
			register("trick_blink_eidos_reversal", BlinkEidosReversalTrick.class, LibPieceGroups.MOVEMENT);
			register("trick_acceleration", AccelerationTrick.class, LibPieceGroups.MOVEMENT);
			register("trick_mass_acceleration", MassAccelerationTrick.class, LibPieceGroups.MOVEMENT);
			register("trick_acceleration_towards_point", AccelerationTowardsPointTrick.class, LibPieceGroups.MOVEMENT);
			register("trick_elastic_anchor", ElasticAnchorTrick.class, LibPieceGroups.MOVEMENT);
			register("trick_place_dropped_block", PlaceDroppedBlockTrick.class, LibPieceGroups.BLOCK_WORKS);
			
			register("trick_move_spirit", MoveSpiritTrick.class, Groups.psionManipulation);
			register("trick_focus_spirit", FocusSpiritTrick.class, Groups.psionManipulation);
			register("trick_defocus_spirit", DefocusSpiritTrick.class, Groups.psionManipulation);
			
			register("trick_early_evaluate", EarlyEvaluateTrick.class, Groups.dataFlow, true);
			register("trick_reevaluate", ReevaluateTrick.class, Groups.dataFlow);
			
			register("trick_spin_item_chamber", SpinItemChamberTrick.class, LibPieceGroups.MISC_TRICKS);
			
			register("trick_save_vector_component", SaveVectorComponentTrick.class, Groups.dataFlow);
			register("trick_write_vector_storage", WriteVectorStorageTrick.class, Groups.dataFlow);
			register("trick_write_text_storage", WriteTextStorageTrick.class, Groups.dataFlow);
			register("trick_write_text_display", WriteTextDisplayTrick.class, Groups.dataFlow);
			register("trick_set_comparator_output", SetComparatorOutputTrick.class, Groups.dataFlow);
			register("trick_set_time", SetTimeTrick.class, Groups.dataFlow);
			
			register("selector_nearby_markers", NearbyMarkersSelector.class, Groups.opticalMagic);
			register("selector_nearby_burning", NearbyBurningSelector.class, LibPieceGroups.ENTITIES_INTRO);
			
			register("selector_nearby_spirits", NearbySpiritsSelector.class, Groups.psionManipulation);
			register("selector_summoned_spirits", SummonedSpiritsSelector.class, Groups.psionManipulation);
			
			register("selector_saved_vector_component", SavedVectorComponentSelector.class, Groups.dataFlow);
			register("selector_read_vector_storage", ReadVectorStorageSelector.class, Groups.dataFlow);
			register("selector_read_text_storage", ReadTextStorageSelector.class, Groups.dataFlow);
			
			register("selector_spell_name", SpellNameSelector.class, Groups.text);
			register("selector_caster_speech", CasterSpeechSelector.class, Groups.text);
			register("selector_nearby_speech", NearbySpeechSelector.class, Groups.text);
			
			register("operator_to_degrees", ToDegreesOperator.class, Groups.math, true);
			register("operator_to_radians", ToRadiansOperator.class, Groups.math);
			register("operator_multiply_accumulate", MultiplyAccumulateOperator.class, Groups.math);
			register("operator_difference", DifferenceOperator.class, Groups.math);
			register("operator_div_mod", DivModOperator.class, Groups.math);
			register("operator_signum_positive_zero", SignumPositiveZeroOperator.class, Groups.math);
			register("operator_signum_negative_zero", SignumNegativeZeroOperator.class, Groups.math);
			register("operator_extract_digit", ExtractDigitOperator.class, Groups.math);
			// register("operator_replace_digit", ReplaceDigitOperator.class, Groups.math);
			
			register("operator_defaulted_vector_construct", DefaultedVectorConstructOperator.class, Groups.dataFlow);
			register("operator_vector_component_wise_multiply", ComponentWiseMultiplyVectorOperator.class, Groups.math, false);
			register("operator_split_vector", SplitVectorOperator.class, Groups.dataFlow);
			register("operator_replace_vector_component", ReplaceVectorComponentOperator.class, Groups.dataFlow);
			register("operator_precise_raycast", PreciseRaycastOperator.class, LibPieceGroups.BLOCK_WORKS);
			register("operator_offset_raycast", OffsetRaycastOperator.class, LibPieceGroups.BLOCK_WORKS);
			register("operator_nearest_axial_vector", NearestAxialVectorOperator.class, Groups.math);
			register("operator_round_vector", RoundVectorOperator.class, Groups.math);
			register("operator_truncate_vector", TruncateVectorOperator.class, Groups.math);
			register("operator_clamp_vector", ClampVectorOperator.class, Groups.math);
			register("operator_focused_block", FocusedBlockOperator.class, LibPieceGroups.ENTITIES_INTRO);
			register("operator_focused_block_face", FocusedBlockFaceOperator.class, LibPieceGroups.ENTITIES_INTRO);
			
			register("operator_entity_sneak_status", EntitySneakStatusOperator.class, LibPieceGroups.ENTITIES_INTRO);
			register("operator_entity_name", EntityNameOperator.class, LibPieceGroups.ENTITIES_INTRO);
			register("operator_entity_eye_position", EntityEyePositionOperator.class, LibPieceGroups.ENTITIES_INTRO);
			register("operator_entity_foot_position", EntityFootPositionOperator.class, LibPieceGroups.ENTITIES_INTRO);
			
			register("operator_as_text", AsTextOperator.class, Groups.text);
			register("operator_text_length", TextLengthOperator.class, Groups.text);
			register("operator_append_text", AppendTextOperator.class, Groups.text);
			register("operator_character_code_at", CharacterCodeAtOperator.class, Groups.text);
			register("operator_character_from_code", CharacterFromCodeOperator.class, Groups.text);
			register("operator_lower_case", LowerCaseOperator.class, Groups.text);
			register("operator_upper_case", UpperCaseOperator.class, Groups.text);
			register("operator_split_text", SplitTextOperator.class, Groups.text);
			register("operator_split_text_at", SplitTextAtOperator.class, Groups.text);
			register("operator_number_from_text", NumberFromTextOperator.class, Groups.text);
			register("operator_select_text", SelectTextOperator.class, Groups.text);
			
			register("operator_hash", HashOperator.class, Groups.dataFlow);
			
			register("operator_error_catcher", ErrorCatcherOperator.class, Groups.dataFlow);
			register("operator_propagate_error", PropagateErrorOperator.class, Groups.dataFlow);
			register("operator_error_status", ErrorStatusOperator.class, Groups.dataFlow);
			
			register("connector_clockwise", ClockwiseConnector.class, Groups.dataFlow);
			register("connector_counterclockwise", CounterclockwiseConnector.class, Groups.dataFlow);
			register("connector_bidirectional", BidirectionalConnector.class, Groups.dataFlow);
			register("connector_in_out", InOutConnector.class, Groups.dataFlow);
			register("connector_bridge", BridgeConnector.class, Groups.dataFlow);
			
			register("constant_text", TextConstant.class, Groups.text, true);
			register("constant_character_code", CharacterCodeConstant.class, Groups.text);
			register("constant_formatting_code", FormattingCodeConstant.class, Groups.text);
			register("constant_line_break", LineBreakConstant.class, Groups.text);
			register("constant_vector", VectorConstant.class, Groups.dataFlow);
		});
	}
	
	public static void register(String id, Class<? extends SpellPiece> piece, String group) {
		register(id, piece, group, false);
	}
	
	public static void register(String id, Class<? extends SpellPiece> piece, String group, boolean main) {
		PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(Phi.modId, id), piece);
		PsiAPI.addPieceToGroup(piece, new ResourceLocation(Phi.modId, group), main);
	}
	
}
