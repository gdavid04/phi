package gdavid.phi.block;

import com.google.common.collect.ImmutableSet;
import gdavid.phi.block.tile.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;

@EventBusSubscriber(bus = Bus.MOD)
public class ModBlocks {
	
	public static Block shadow, mpu, vsu, textsu, cadHolder, spellStorage, textDisplay, cable, spellDisplay, infusionLaser, distillChamberWall, distillChamberController, psimetalCrusher, psionicDustOre;
	
	public static PoiType mpuPOI;
	
	@SubscribeEvent
	public static void init(RegisterEvent event) {
		event.register(Keys.BLOCKS, handler -> {
			handler.register(ShadowBlock.id, shadow = new ShadowBlock());
			handler.register(MPUBlock.id, mpu = new MPUBlock());
			handler.register(VSUBlock.id, vsu = new VSUBlock());
			handler.register(TextSUBlock.id, textsu = new TextSUBlock());
			handler.register(CADHolderBlock.id, cadHolder = new CADHolderBlock());
			handler.register(SpellStorageBlock.id, spellStorage = new SpellStorageBlock());
			handler.register(TextDisplayBlock.id, textDisplay = new TextDisplayBlock());
			handler.register(CableBlock.id, cable = new CableBlock());
			handler.register(SpellDisplayBlock.id, spellDisplay = new SpellDisplayBlock());
			handler.register(InfusionLaserBlock.id, infusionLaser = new InfusionLaserBlock());
			handler.register(DistillChamberWallBlock.id, distillChamberWall = new DistillChamberWallBlock());
			handler.register(DistillChamberControllerBlock.id, distillChamberController = new DistillChamberControllerBlock());
			handler.register(PsimetalCrusherBlock.id, psimetalCrusher = new PsimetalCrusherBlock());
			handler.register("psionic_dust_ore", psionicDustOre = new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND).requiresCorrectToolForDrops()
					/* TODO fix before 1.19 release
					.harvestLevel(Tiers.NETHERITE.getLevel()).harvestTool(ToolType.PICKAXE).strength(3.0F, 9.0F)
					*/));
		});
		event.register(Keys.ITEMS, handler -> {
			handler.register(MPUBlock.id, new BlockItem(mpu, new Item.Properties().rarity(Rarity.RARE).tab(CreativeModeTab.TAB_MISC)));
			handler.register(VSUBlock.id, new BlockItem(vsu, new Item.Properties().rarity(Rarity.UNCOMMON).tab(CreativeModeTab.TAB_MISC)));
			handler.register(TextSUBlock.id, new BlockItem(textsu, new Item.Properties().rarity(Rarity.UNCOMMON).tab(CreativeModeTab.TAB_MISC)));
			handler.register(CADHolderBlock.id, new BlockItem(cadHolder, new Item.Properties().rarity(Rarity.UNCOMMON).tab(CreativeModeTab.TAB_MISC)));
			handler.register(SpellStorageBlock.id, new BlockItem(spellStorage, new Item.Properties().rarity(Rarity.UNCOMMON).tab(CreativeModeTab.TAB_MISC)));
			handler.register(TextDisplayBlock.id, new BlockItem(textDisplay, new Item.Properties().rarity(Rarity.UNCOMMON).tab(CreativeModeTab.TAB_MISC)));
			handler.register(CableBlock.id, new BlockItem(cable, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
			handler.register(SpellDisplayBlock.id, new BlockItem(spellDisplay, new Item.Properties().rarity(Rarity.UNCOMMON).tab(CreativeModeTab.TAB_MISC)));
			handler.register(InfusionLaserBlock.id, new BlockItem(infusionLaser, new Item.Properties().rarity(Rarity.EPIC).tab(CreativeModeTab.TAB_MISC)));
			handler.register(DistillChamberWallBlock.id, new BlockItem(distillChamberWall, new Item.Properties().rarity(Rarity.EPIC).tab(CreativeModeTab.TAB_MISC)));
			handler.register(DistillChamberControllerBlock.id, new BlockItem(distillChamberController, new Item.Properties().rarity(Rarity.EPIC).tab(CreativeModeTab.TAB_MISC)));
			handler.register(PsimetalCrusherBlock.id, new BlockItem(psimetalCrusher, new Item.Properties().rarity(Rarity.UNCOMMON).tab(CreativeModeTab.TAB_MISC)));
			handler.register("psionic_dust_ore", new BlockItem(psionicDustOre, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
		});
		event.register(Keys.BLOCK_ENTITY_TYPES, handler -> {
			handler.register(MPUBlock.id, MPUTile.type = BlockEntityType.Builder.of(MPUTile::new, mpu).build(null));
			handler.register(VSUBlock.id, VSUTile.type = BlockEntityType.Builder.of(VSUTile::new, vsu).build(null));
			handler.register(TextSUBlock.id, TextSUTile.type = BlockEntityType.Builder.of(TextSUTile::new, textsu).build(null));
			handler.register(CADHolderBlock.id, CADHolderTile.type = BlockEntityType.Builder.of(CADHolderTile::new, cadHolder).build(null));
			handler.register(SpellStorageBlock.id, SpellStorageTile.type = BlockEntityType.Builder.of(SpellStorageTile::new, spellStorage).build(null));
			handler.register(TextDisplayBlock.id, TextDisplayTile.type = BlockEntityType.Builder.of(TextDisplayTile::new, textDisplay).build(null));
			handler.register(CableBlock.id, CableTile.type = BlockEntityType.Builder.of(CableTile::new, cable).build(null));
			handler.register(SpellDisplayBlock.id, SpellDisplayTile.type = BlockEntityType.Builder.of(SpellDisplayTile::new, spellDisplay).build(null));
			handler.register(InfusionLaserBlock.id, InfusionLaserTile.type = BlockEntityType.Builder.of(InfusionLaserTile::new, infusionLaser).build(null));
			handler.register(DistillChamberControllerBlock.id, DistillChamberControllerTile.type = BlockEntityType.Builder.of(DistillChamberControllerTile::new, distillChamberController).build(null));
			handler.register(PsimetalCrusherBlock.id, PsimetalCrusherTile.type = BlockEntityType.Builder.of(PsimetalCrusherTile::new, psimetalCrusher).build(null));
		});
		event.register(Keys.POI_TYPES, handler -> {
			handler.register("mpu", mpuPOI = new PoiType(ImmutableSet.of(mpu.defaultBlockState()), 0, 1));
		});
	}
	
}
