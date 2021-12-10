package gdavid.phi.item;

import gdavid.phi.block.MPUBlock;
import gdavid.phi.capability.MPUCADData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.cad.ICADData;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceCraftingTrick;

public class MPUCAD extends Item implements ICAD {
	
	public static final MPUCAD instance = new MPUCAD();
	
	public static int savedVectors = 1;
	
	private MPUCAD() {
		super(new Properties());
		setRegistryName(MPUBlock.id + ".cad");
	}
	
	public ICADData getData(ItemStack stack) {
		return stack.getCapability(PsiAPI.CAD_DATA_CAPABILITY).orElseGet(() -> new MPUCADData(stack));
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		MPUCADData data = new MPUCADData(stack);
		if (nbt != null) {
			data.deserializeNBT(nbt);
		}
		return data;
	}
	
	@Override
	public ItemStack getComponentInSlot(ItemStack stack, EnumCADComponent type) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public int getStatValue(ItemStack stack, EnumCADStat stat) {
		if (stat == EnumCADStat.EFFICIENCY) return 100;
		if (stat == EnumCADStat.POTENCY) return 80;
		if (stat == EnumCADStat.COMPLEXITY) return 40;
		if (stat == EnumCADStat.PROJECTION) return 3;
		if (stat == EnumCADStat.BANDWIDTH) return 9;
		if (stat == EnumCADStat.SAVED_VECTORS) return savedVectors;
		return 0;
	}
	
	@Override
	public int getStoredPsi(ItemStack stack) {
		return 0;
	}
	
	@Override
	public void regenPsi(ItemStack stack, int psi) {
	}
	
	@Override
	public int consumePsi(ItemStack stack, int psi) {
		return psi;
	}
	
	@Override
	public int getMemorySize(ItemStack stack) {
		return savedVectors;
	}
	
	@Override
	public void setStoredVector(ItemStack stack, int memorySlot, Vector3 vec) throws SpellRuntimeException {
		if (memorySlot < 0 || memorySlot >= savedVectors) {
			throw new SpellRuntimeException(SpellRuntimeException.MEMORY_OUT_OF_BOUNDS);
		}
		getData(stack).setSavedVector(memorySlot, vec);
	}
	
	@Override
	public Vector3 getStoredVector(ItemStack stack, int memorySlot) throws SpellRuntimeException {
		if (memorySlot < 0 || memorySlot >= savedVectors) {
			throw new SpellRuntimeException(SpellRuntimeException.MEMORY_OUT_OF_BOUNDS);
		}
		return getData(stack).getSavedVector(memorySlot);
	}
	
	@Override
	public int getTime(ItemStack stack) {
		return getData(stack).getTime();
	}
	
	public void setTime(ItemStack stack, int time) {
		ICADData data = getData(stack);
		data.setTime(time);
	}
	
	@Override
	public void incrementTime(ItemStack stack) {
		ICADData data = getData(stack);
		data.setTime(data.getTime() + 1);
	}
	
	@Override
	public int getSpellColor(ItemStack stack) {
		return ICADColorizer.DEFAULT_SPELL_COLOR;
	}
	
	@Override
	public boolean craft(ItemStack cad, PlayerEntity entity, PieceCraftingTrick trick) {
		// MPUs can't craft
		return false;
	}
	
}
