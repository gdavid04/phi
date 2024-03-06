package gdavid.phi.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;

@EventBusSubscriber(bus = Bus.MOD)
public class ModItems {
	
	@SubscribeEvent
	public static void init(RegisterEvent event) {
		event.register(Keys.ITEMS, handler -> {
			handler.register("basic_spell_magazine", new SpellMagazineItem("basic_spell_magazine", 3, 5, 1));
			handler.register("large_spell_magazine", new SpellMagazineItem("large_spell_magazine", 6, 7, 2));
			handler.register("huge_spell_magazine", new SpellMagazineItem("huge_spell_magazine", 9, 9, 3));
			
			handler.register("wide_band_spell_magazine", new SpellMagazineItem("wide_band_spell_magazine", 1, 9, 2));
			
			handler.register("increased_storage_spell_magazine", new SpellMagazineItem("increased_storage_spell_magazine", 5, 9, 9));
			handler.register("bulk_storage_spell_magazine", new SpellMagazineItem("bulk_storage_spell_magazine", 3, 9, 15));
			
			handler.register("smart_spell_magazine", new SmartSpellMagazineItem("smart_spell_magazine", 3, 9, 2));
			handler.register("compound_spell_magazine", new CompoundSpellMagazineItem("compound_spell_magazine", 3, 9, 2));
			
			handler.register("blood_converter", new BloodConverterItem("blood_converter", 0.5f));
			
			handler.register("spirit_summoning_talisman", new SpiritSummoningTalismanItem("spirit_summoning_talisman"));
			
			handler.register("psionic_dust", new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.EPIC)));
			handler.register("psionized_netherite_plate", new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE)));
			
			handler.register(MPUCAD.id, MPUCAD.instance);
		});
	}
	
}
