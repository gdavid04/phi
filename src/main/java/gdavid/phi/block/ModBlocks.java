package gdavid.phi.block;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD)
public class ModBlocks {
	
	public static final Block shadow = new ShadowBlock();
	public static final Block mpu = new MPUBlock();
	public static final Block vsu = new VSUBlock();
	public static final Block textsu = new TextSUBlock();
	public static final Block cadHolder = new CADHolderBlock();
	public static final Block spellStorage = new SpellStorageBlock();
	public static final Block textDisplay = new TextDisplayBlock();
	public static final Block cable = new CableBlock();
	public static final Block spellDisplay = new SpellDisplayBlock();
	public static final Block infusionLaser = new InfusionLaserBlock();
	public static final Block distillChamberWall = new DistillChamberWallBlock();
	public static final Block distillChamberController = new DistillChamberControllerBlock();
	public static final Block psimetalCrusher = new PsimetalCrusherBlock();
	public static final Block psionicDustOre = new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().harvestLevel(ItemTier.NETHERITE.getHarvestLevel()).harvestTool(
			ToolType.PICKAXE).hardnessAndResistance(3.0F, 9.0F)).setRegistryName("psionic_dust_ore");
	
	public static final PointOfInterestType mpuPOI = new PointOfInterestType(Phi.modId + ":mpu",
			PointOfInterestType.getAllStates(mpu), 0, 1).setRegistryName(Phi.modId, "mpu");
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(shadow, mpu, vsu, textsu, cadHolder, spellStorage, textDisplay, cable, spellDisplay, infusionLaser, distillChamberWall, distillChamberController, psimetalCrusher, psionicDustOre);
	}
	
	@SubscribeEvent
	public static void initItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new BlockItem(mpu, new Item.Properties().rarity(Rarity.RARE).group(ItemGroup.MISC))
						.setRegistryName(mpu.getRegistryName()),
				new BlockItem(vsu, new Item.Properties().rarity(Rarity.UNCOMMON).group(ItemGroup.MISC))
						.setRegistryName(vsu.getRegistryName()),
				new BlockItem(textsu, new Item.Properties().rarity(Rarity.UNCOMMON).group(ItemGroup.MISC))
						.setRegistryName(textsu.getRegistryName()),
				new BlockItem(cadHolder, new Item.Properties().rarity(Rarity.UNCOMMON).group(ItemGroup.MISC))
						.setRegistryName(cadHolder.getRegistryName()),
				new BlockItem(spellStorage, new Item.Properties().rarity(Rarity.UNCOMMON).group(ItemGroup.MISC))
						.setRegistryName(spellStorage.getRegistryName()),
				new BlockItem(textDisplay, new Item.Properties().rarity(Rarity.UNCOMMON).group(ItemGroup.MISC))
						.setRegistryName(textDisplay.getRegistryName()),
				new BlockItem(cable, new Item.Properties().group(ItemGroup.MISC))
						.setRegistryName(cable.getRegistryName()),
				new BlockItem(spellDisplay, new Item.Properties().rarity(Rarity.UNCOMMON).group(ItemGroup.MISC))
						.setRegistryName(spellDisplay.getRegistryName()),
				new BlockItem(infusionLaser, new Item.Properties().rarity(Rarity.EPIC).group(ItemGroup.MISC))
						.setRegistryName(infusionLaser.getRegistryName()),
				new BlockItem(distillChamberWall, new Item.Properties().rarity(Rarity.EPIC).group(ItemGroup.MISC))
						.setRegistryName(distillChamberWall.getRegistryName()),
				new BlockItem(distillChamberController, new Item.Properties().rarity(Rarity.EPIC).group(ItemGroup.MISC))
						.setRegistryName(distillChamberController.getRegistryName()),
				new BlockItem(psimetalCrusher, new Item.Properties().rarity(Rarity.UNCOMMON).group(ItemGroup.MISC))
						.setRegistryName(psimetalCrusher.getRegistryName()),
				new BlockItem(psionicDustOre, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
						.setRegistryName(psionicDustOre.getRegistryName()));
	}
	
	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public static void initTiles(RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().registerAll(
				MPUTile.type = (TileEntityType<MPUTile>) TileEntityType.Builder.create(MPUTile::new, mpu).build(null)
						.setRegistryName(mpu.getRegistryName()),
				VSUTile.type = (TileEntityType<VSUTile>) TileEntityType.Builder.create(VSUTile::new, vsu).build(null)
						.setRegistryName(vsu.getRegistryName()),
				TextSUTile.type = (TileEntityType<TextSUTile>) TileEntityType.Builder.create(TextSUTile::new, textsu)
						.build(null).setRegistryName(textsu.getRegistryName()),
				CADHolderTile.type = (TileEntityType<CADHolderTile>) TileEntityType.Builder
						.create(CADHolderTile::new, cadHolder).build(null).setRegistryName(cadHolder.getRegistryName()),
				SpellStorageTile.type = (TileEntityType<SpellStorageTile>) TileEntityType.Builder
						.create(SpellStorageTile::new, spellStorage).build(null)
						.setRegistryName(spellStorage.getRegistryName()),
				TextDisplayTile.type = (TileEntityType<TextDisplayTile>) TileEntityType.Builder
						.create(TextDisplayTile::new, textDisplay).build(null)
						.setRegistryName(textDisplay.getRegistryName()),
				CableTile.type = (TileEntityType<CableTile>) TileEntityType.Builder.create(CableTile::new, cable)
						.build(null).setRegistryName(cable.getRegistryName()),
				SpellDisplayTile.type = (TileEntityType<SpellDisplayTile>) TileEntityType.Builder.create(SpellDisplayTile::new, spellDisplay)
						.build(null).setRegistryName(spellDisplay.getRegistryName()),
				InfusionLaserTile.type = (TileEntityType<InfusionLaserTile>) TileEntityType.Builder.create(InfusionLaserTile::new, infusionLaser)
						.build(null).setRegistryName(infusionLaser.getRegistryName()),
				DistillChamberControllerTile.type = (TileEntityType<DistillChamberControllerTile>) TileEntityType.Builder.create(DistillChamberControllerTile::new, distillChamberController)
						.build(null).setRegistryName(distillChamberController.getRegistryName()),
				PsimetalCrusherTile.type = (TileEntityType<PsimetalCrusherTile>) TileEntityType.Builder.create(PsimetalCrusherTile::new, psimetalCrusher)
						.build(null).setRegistryName(psimetalCrusher.getRegistryName()));
	}
	
	@SubscribeEvent
	public static void initPOIs(RegistryEvent.Register<PointOfInterestType> event) {
		event.getRegistry().registerAll(mpuPOI);
	}
	
}
