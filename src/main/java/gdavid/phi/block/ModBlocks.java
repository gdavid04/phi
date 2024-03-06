package gdavid.phi.block;

import com.google.common.collect.ImmutableSet;
import gdavid.phi.Phi;
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
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashSet;

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
	public static final Block psionicDustOre = new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND).requiresCorrectToolForDrops().harvestLevel(Tiers.NETHERITE.getLevel()).harvestTool(
			ToolType.PICKAXE).strength(3.0F, 9.0F));
	
	public static final PoiType mpuPOI = new PoiType(ImmutableSet.of(mpu.defaultBlockState()), 0, 1);
	
	@SubscribeEvent
	public static void init(RegisterEvent event) {
		event.register(Keys.BLOCKS, handler -> {
			handler.register(ShadowBlock.id, shadow);
			handler.register(MPUBlock.id, mpu);
			handler.register(VSUBlock.id, vsu);
			handler.register(TextSUBlock.id, textsu);
			handler.register(CADHolderBlock.id, cadHolder);
			handler.register(SpellStorageBlock.id, spellStorage);
			handler.register(TextDisplayBlock.id, textDisplay);
			handler.register(CableBlock.id, cable);
			handler.register(SpellDisplayBlock.id, spellDisplay);
			handler.register(InfusionLaserBlock.id, infusionLaser);
			handler.register(DistillChamberWallBlock.id, distillChamberWall);
			handler.register(DistillChamberControllerBlock.id, distillChamberController);
			handler.register(PsimetalCrusherBlock.id, psimetalCrusher);
			handler.register("psionic_dust_ore", psionicDustOre);
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
			handler.register("mpu", mpuPOI);
		});
	}
	
}
