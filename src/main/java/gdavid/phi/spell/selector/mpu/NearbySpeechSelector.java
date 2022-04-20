package gdavid.phi.spell.selector.mpu;

import gdavid.phi.block.ModBlocks;
import gdavid.phi.block.tile.MPUTile;
import gdavid.phi.block.tile.MPUTile.MPUCaster;
import gdavid.phi.spell.Errors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestManager.Status;
import net.minecraft.world.server.ServerWorld;
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
		PlayerEntity player = event.getPlayer();
		if (!(player.world instanceof ServerWorld)) return;
		BlockPos pos = player.getPosition();
		PointOfInterestManager poiManager = ((ServerWorld) player.world).getPointOfInterestManager();
		poiManager.func_219146_b(type -> type == ModBlocks.mpuPOI, pos, (int) SpellContext.MAX_DISTANCE, Status.ANY).forEach(poi -> {
			TileEntity tile = player.world.getTileEntity(poi.getPos());
			if (tile instanceof MPUTile) ((MPUTile) tile).setNearbySpeech(event.getMessage());
		});
	}
	
}
