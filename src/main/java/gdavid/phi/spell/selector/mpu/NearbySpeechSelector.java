package gdavid.phi.spell.selector.mpu;

import gdavid.phi.block.ModBlocks;
import gdavid.phi.block.tile.MPUTile;
import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.spell.Errors;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

@EventBusSubscriber
public class NearbySpeechSelector extends PieceSelector {
	
	public NearbySpeechSelector(Spell spell) {
		super(spell);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (!(context.caster instanceof MPUCaster)) Errors.noMpu.runtime();
		return ((MPUCaster) context.caster).getNearbySpeech();
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
	@SubscribeEvent
	public static void speech(ServerChatEvent event) {
		Player player = event.getPlayer();
		if (!(player.level instanceof ServerLevel)) return;
		BlockPos pos = player.blockPosition();
		PoiManager poiManager = ((ServerLevel) player.level).getPoiManager();
		poiManager.getInRange(type -> type.get() == ModBlocks.mpuPOI, pos, (int) SpellContext.MAX_DISTANCE, Occupancy.ANY)
				.forEach(poi -> {
					BlockEntity tile = player.level.getBlockEntity(poi.getPos());
					if (tile instanceof MPUTile) ((MPUTile) tile).setNearbySpeech(event.getRawText());
				});
	}
	
}
