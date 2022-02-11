package gdavid.phi.item;

import java.lang.reflect.Field;
import java.util.function.Consumer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.SpellCastEvent;
import vazkii.psi.api.spell.SpellContext;

@EventBusSubscriber
public class CompoundSpellMagazineItem extends SpellMagazineItem {
	
	public CompoundSpellMagazineItem(String id, int sockets, int bandwidth, int vectors) {
		super(id, sockets, bandwidth, vectors);
	}
	
	static final ThreadLocal<Boolean> compoundCasting = ThreadLocal.withInitial(() -> false);
	
	@SubscribeEvent
	public static void cast(SpellCastEvent event) {
		if (compoundCasting.get()) return;
		ItemStack item = event.cad;
		if (!event.context.tool.isEmpty() || event.context.focalPoint != event.player || !(item.getItem() instanceof ICAD) || !ISocketable.isSocketable(item)) return;
		ICAD cad = (ICAD) item.getItem();
		Item socket = cad.getComponentInSlot(item, EnumCADComponent.SOCKET).getItem();
		if (socket instanceof CompoundSpellMagazineItem) {
			compoundCasting.set(true);
			ISocketable socketable = ISocketable.socketable(item);
			try {
				Field overflow = Class.forName("vazkii.psi.common.core.handler.PlayerDataHandler$PlayerData").getField("overflowed");
				boolean didOverflow = overflow.getBoolean(event.playerData);
				int index = 1;
				for (int i = 0; i < ((CompoundSpellMagazineItem) socket).sockets; i++) {
					if (i == socketable.getSelectedSlot()) continue;
					overflow.set(event.playerData, false); // ignore mid-cast overflows
					final int currentIndex = index++;
					Class.forName("vazkii.psi.common.item.ItemCAD")
							.getMethod("cast", World.class, PlayerEntity.class, Class.forName("vazkii.psi.common.core.handler.PlayerDataHandler$PlayerData"), ItemStack.class, ItemStack.class, int.class, int.class, float.class, Consumer.class)
							.invoke(null, event.context.caster.world, event.player, event.playerData, socketable.getBulletInSocket(i), event.cad, 0, 10, 0, (Consumer<SpellContext>) (SpellContext ctx) -> ctx.loopcastIndex = currentIndex);
					didOverflow |= overflow.getBoolean(event.playerData);
				}
				overflow.set(event.playerData, overflow.getBoolean(event.playerData) || didOverflow);
			} catch (Exception e) {
				e.printStackTrace();
			}
			compoundCasting.set(false);
		}
	}
	
}
