package gdavid.phi.block;

import gdavid.phi.block.tile.MPUTile;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD)
public class ModBlocks {

	public static final Block shadow = new ShadowBlock();
	public static final Block mpu = new MPUBlock();
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(shadow, mpu);
	}
	
	@SubscribeEvent
	public static void initItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(new BlockItem(mpu, new Item.Properties().rarity(Rarity.RARE).group(ItemGroup.MISC)).setRegistryName(mpu.getRegistryName()));
	}
	
	@SubscribeEvent
	public static void initTiles(RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().registerAll(MPUTile.type = TileEntityType.Builder.create(MPUTile::new, mpu).build(null).setRegistryName(mpu.getRegistryName()));
	}
	
}
