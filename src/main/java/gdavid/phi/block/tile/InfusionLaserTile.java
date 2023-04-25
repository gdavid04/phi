package gdavid.phi.block.tile;

import gdavid.phi.block.InfusionLaserBlock;
import gdavid.phi.util.IPsiAcceptor;
import gdavid.phi.util.IWaveImpacted;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.recipe.ITrickRecipe;
import vazkii.psi.api.spell.piece.PieceCraftingTrick;
import vazkii.psi.common.crafting.ModCraftingRecipes;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageVisualEffect;
import vazkii.psi.common.spell.trick.infusion.PieceTrickInfusion;

import java.util.List;
import java.util.Optional;

public class InfusionLaserTile extends TileEntity implements IWaveImpacted, IPsiAcceptor {
	
	public static TileEntityType<InfusionLaserTile> type;
	
	public static final String tagPsi = "psi";
	public static final String tagCad = "cad";
	
	public int psi;
	
	public InfusionLaserTile() {
		super(type);
	}
	
	@Override
	public void addPsi(int amount) {
		if (amount == 0) return;
		psi = Math.max(0, psi + amount);
		int capacity = getPsiCapacity();
		if (psi >= capacity) {
			infuse();
			psi -= capacity;
		}
		markDirty();
	}
	
	public int getPsiCapacity() {
		return 1200; // Trick: Infusion cost
	}
	
	public void infuse() {
		// Emulate CAD crafting but limited to 1 item per cast
		if (world.isRemote) return;
		Direction dir = getBlockState().get(InfusionLaserBlock.FACING);
		Vector3d focus = Vector3d.copyCentered(pos.offset(dir));
		List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, AxisAlignedBB.withSizeAtOrigin(1, 1, 1).offset(focus));
		RecipeWrapper wrapper = new RecipeWrapper(new ItemStackHandler(1));
		PieceCraftingTrick piece = new PieceTrickInfusion(null);
		for (ItemEntity item : items) {
			ItemStack stack = item.getItem();
			wrapper.setInventorySlotContents(0, stack);
			Optional<ITrickRecipe> recipe = world.getRecipeManager().getRecipe(ModCraftingRecipes.TRICK_RECIPE_TYPE, wrapper, world)
					.filter(r -> r.getPiece() == null || r.getPiece().canCraft(piece));
			if (recipe.isPresent()) {
				MessageRegister.HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> item), new MessageVisualEffect(
						ICADColorizer.DEFAULT_SPELL_COLOR, item.getPosX(), item.getPosY(), item.getPosZ(),
						item.getWidth(), item.getHeight(), item.getYOffset(), MessageVisualEffect.TYPE_CRAFT
				));
				stack.shrink(1);
				if (stack.isEmpty()) item.remove();
				else item.setItem(stack);
				ItemStack result = recipe.get().getRecipeOutput().copy();
				world.addEntity(new ItemEntity(world, item.getPosX(), item.getPosY(), item.getPosZ(), result));
				return;
			}
		}
		MessageRegister.HANDLER.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MessageVisualEffect(
				ICADColorizer.DEFAULT_SPELL_COLOR, focus.x, focus.y, focus.z,
				0.25f, 0.25f, 0, MessageVisualEffect.TYPE_CRAFT
		));
	}
	
	@Override
	public void waveImpact(Float frequency, float focus) {
		addPsi(-Math.round(frequency * focus * 4));
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		read(nbt);
	}
	
	public void read(CompoundNBT nbt) {
		psi = nbt.getInt(tagPsi);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.putInt(tagPsi, psi);
		return nbt;
	}
	
}
