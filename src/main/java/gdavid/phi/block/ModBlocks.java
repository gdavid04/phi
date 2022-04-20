package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.MPUTile;
import gdavid.phi.block.tile.VSUTile;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD)
public class ModBlocks {
	
	public static final Block shadow = new ShadowBlock();
	public static final Block mpu = new MPUBlock();
	public static final Block vsu = new VSUBlock();
	
	public static final PointOfInterestType mpuPOI = new PointOfInterestType(Phi.modId + ":mpu", PointOfInterestType.getAllStates(mpu), 0, 1).setRegistryName(Phi.modId, "mpu");
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(shadow, mpu, vsu);
	}
	
	@SubscribeEvent
	public static void initItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new BlockItem(mpu, new Item.Properties().rarity(Rarity.RARE).group(ItemGroup.MISC))
						.setRegistryName(mpu.getRegistryName()),
				new BlockItem(vsu, new Item.Properties().rarity(Rarity.UNCOMMON).group(ItemGroup.MISC))
						.setRegistryName(vsu.getRegistryName()));
	}
	
	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public static void initTiles(RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().registerAll(
				MPUTile.type = (TileEntityType<MPUTile>) TileEntityType.Builder.create(MPUTile::new, mpu).build(null)
						.setRegistryName(mpu.getRegistryName()),
				VSUTile.type = (TileEntityType<VSUTile>) TileEntityType.Builder.create(VSUTile::new, vsu).build(null)
						.setRegistryName(vsu.getRegistryName()));
	}
	
	@SubscribeEvent
	public static void initPOIs(RegistryEvent.Register<PointOfInterestType> event) {
		event.getRegistry().registerAll(mpuPOI);
	}
	
}
