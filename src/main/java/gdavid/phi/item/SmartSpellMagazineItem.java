package gdavid.phi.item;

import gdavid.phi.Phi;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.LoopcastEndEvent;
import vazkii.psi.api.spell.PreSpellCastEvent;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;

@EventBusSubscriber
public class SmartSpellMagazineItem extends SpellMagazineItem {
	
	public static final String tagSmartResetSlot = Phi.modId + ":smart_reset_slot";
	
	public SmartSpellMagazineItem(String id, int sockets, int bandwidth, int vectors) {
		super(id, sockets, bandwidth, vectors);
	}
	
	@SubscribeEvent
	public static void preCast(PreSpellCastEvent event) {
		// Heuristic to get manual CAD casts while remaining compatible with addon assemblies
		if (!event.getContext().tool.isEmpty()) return;
		ItemStack item = event.getCad();
		if (!(item.getItem() instanceof ICAD cad) || !ISocketable.isSocketable(item)) return;
		ItemStack socket = cad.getComponentInSlot(item, EnumCADComponent.SOCKET);
		if (socket.getItem() instanceof SmartSpellMagazineItem) {
			item.getCapability(PsiAPI.SOCKETABLE_CAPABILITY).ifPresent(sock -> {
				event.getPlayerData().getCustomData().putInt(tagSmartResetSlot, sock.getSelectedSlot());
			});
		}
	}
	
	@SubscribeEvent
	public static void loopcastEnd(LoopcastEndEvent event) {
		if (!(((PlayerData) event.getPlayerData()).lastTickLoopcastStack.getItem() instanceof ICAD)) return;
		ItemStack item = PsiAPI.getPlayerCAD(event.getPlayer());
		if (!(item.getItem() instanceof ICAD cad) || !ISocketable.isSocketable(item)) return;
		ItemStack socket = cad.getComponentInSlot(item, EnumCADComponent.SOCKET);
		if (socket.getItem() instanceof SmartSpellMagazineItem) {
			var data = event.getPlayerData().getCustomData();
			int slot = data.contains(tagSmartResetSlot) ? data.getInt(tagSmartResetSlot) : 0;
			ISocketable.socketable(item).setSelectedSlot(slot);
		}
	}
	
}
