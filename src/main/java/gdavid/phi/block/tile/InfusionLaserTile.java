package gdavid.phi.block.tile;

import gdavid.phi.block.InfusionLaserBlock;
import gdavid.phi.util.IPsiAcceptor;
import gdavid.phi.util.IWaveImpacted;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.network.PacketDistributor;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.recipe.ITrickRecipe;
import vazkii.psi.api.spell.piece.PieceCraftingTrick;
import vazkii.psi.common.crafting.ModCraftingRecipes;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageVisualEffect;
import vazkii.psi.common.spell.trick.infusion.PieceTrickInfusion;

import java.util.List;
import java.util.Optional;

public class InfusionLaserTile extends BlockEntity implements IWaveImpacted, IPsiAcceptor {
	
	public static BlockEntityType<InfusionLaserTile> type;
	
	public static final String tagPsi = "psi";
	public static final String tagCad = "cad";
	
	public int psi;
	
	public InfusionLaserTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
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
		setChanged();
	}
	
	public int getPsiCapacity() {
		return 1200; // Trick: Infusion cost
	}
	
	public void infuse() {
		// Emulate CAD crafting but limited to 1 item per cast
		if (level.isClientSide) return;
		Direction dir = getBlockState().getValue(InfusionLaserBlock.FACING);
		Vec3 focus = Vec3.atCenterOf(worldPosition.relative(dir));
		List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, AABB.ofSize(focus, 1, 1, 1));
		RecipeWrapper wrapper = new RecipeWrapper(new ItemStackHandler(1));
		PieceCraftingTrick piece = new PieceTrickInfusion(null);
		for (ItemEntity item : items) {
			ItemStack stack = item.getItem();
			wrapper.setItem(0, stack);
			Optional<ITrickRecipe> recipe = level.getRecipeManager().getRecipeFor(ModCraftingRecipes.TRICK_RECIPE_TYPE, wrapper, level)
					.filter(r -> r.getPiece() == null || r.getPiece().canCraft(piece));
			if (recipe.isPresent()) {
				MessageRegister.HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> item), new MessageVisualEffect(
						ICADColorizer.DEFAULT_SPELL_COLOR, item.getX(), item.getY(), item.getZ(),
						item.getBbWidth(), item.getBbHeight(), item.getMyRidingOffset(), MessageVisualEffect.TYPE_CRAFT
				));
				stack.shrink(1);
				if (stack.isEmpty()) item.discard();
				else item.setItem(stack);
				ItemStack result = recipe.get().getResultItem().copy();
				level.addFreshEntity(new ItemEntity(level, item.getX(), item.getY(), item.getZ(), result));
				return;
			}
		}
		MessageRegister.HANDLER.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new MessageVisualEffect(
				ICADColorizer.DEFAULT_SPELL_COLOR, focus.x, focus.y, focus.z,
				0.25f, 0.25f, 0, MessageVisualEffect.TYPE_CRAFT
		));
	}
	
	@Override
	public void waveImpact(Float frequency, float focus) {
		addPsi(-Math.round(frequency * focus * 4));
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		psi = nbt.getInt(tagPsi);
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putInt(tagPsi, psi);
	}
	
}
