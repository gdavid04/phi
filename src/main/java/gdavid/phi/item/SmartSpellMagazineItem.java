package gdavid.phi.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.LoopcastEndEvent;

@EventBusSubscriber
public class SmartSpellMagazineItem extends SpellMagazineItem {
	
	public SmartSpellMagazineItem(String id, int sockets, int bandwidth, int vectors) {
		super(id, sockets, bandwidth, vectors);
	}
	
	@SubscribeEvent
	public static void loopcastEnd(LoopcastEndEvent event) {
		ItemStack item = event.getPlayer().getHeldItem(event.getHand());
		if (!(item.getItem() instanceof ICAD) || !ISocketable.isSocketable(item)) return;
		ICAD cad = (ICAD) item.getItem();
		Item socket = cad.getComponentInSlot(item, EnumCADComponent.SOCKET).getItem();
		if (socket instanceof SmartSpellMagazineItem) {
			ISocketable.socketable(item).setSelectedSlot(0);
		}
	}
	
}
