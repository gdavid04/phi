package gdavid.phi.item;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD)
public class ModItems {
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(new SpellMagazineItem("basic_spell_magazine", 3, 5, 1),
				new SpellMagazineItem("large_spell_magazine", 6, 7, 2),
				new SpellMagazineItem("huge_spell_magazine", 9, 9, 3),
				
				new SpellMagazineItem("wide_band_spell_magazine", 1, 9, 2),
				
				new SpellMagazineItem("increased_storage_spell_magazine", 5, 9, 9),
				new SpellMagazineItem("bulk_storage_spell_magazine", 3, 9, 15),
				
				new SmartSpellMagazineItem("smart_spell_magazine", 3, 9, 2),
				new CompoundSpellMagazineItem("compound_spell_magazine", 3, 9, 2),
				
				new BloodConverterItem("blood_converter", 0.5f),
				
				new SpiritSummoningTalismanItem("spirit_summoning_talisman"),
				
				MPUCAD.instance);
	}
	
}
