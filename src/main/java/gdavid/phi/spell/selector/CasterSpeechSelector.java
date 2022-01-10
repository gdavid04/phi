package gdavid.phi.spell.selector;

import java.util.WeakHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

@EventBusSubscriber
public class CasterSpeechSelector extends PieceSelector {
	
	private static WeakHashMap<PlayerEntity, String> lastSaid = new WeakHashMap<>();
	
	public CasterSpeechSelector(Spell spell) {
		super(spell);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return lastSaid.getOrDefault(context.caster, "");
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
	@SubscribeEvent
	public static void speech(ServerChatEvent event) {
		lastSaid.put(event.getPlayer(), event.getMessage());
	}
	
}
