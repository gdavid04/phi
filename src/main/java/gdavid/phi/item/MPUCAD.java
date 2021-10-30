package gdavid.phi.item;

import gdavid.phi.block.MPUBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceCraftingTrick;

public class MPUCAD extends Item implements ICAD {
	
	public static final MPUCAD instance = new MPUCAD();
	
	private MPUCAD() {
		super(new Properties());
		setRegistryName(MPUBlock.id + ".cad");
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
		return 0;
	}

	@Override
	public int getStoredPsi(ItemStack stack) {
		return 0;
	}

	@Override
	public void regenPsi(ItemStack stack, int psi) {}

	@Override
	public int consumePsi(ItemStack stack, int psi) {
		return 0;
	}

	@Override
	public int getMemorySize(ItemStack stack) {
		return 0;
	}

	@Override
	public void setStoredVector(ItemStack stack, int memorySlot, Vector3 vec) throws SpellRuntimeException {
		throw new SpellRuntimeException(SpellRuntimeException.MEMORY_OUT_OF_BOUNDS);
	}

	@Override
	public Vector3 getStoredVector(ItemStack stack, int memorySlot) throws SpellRuntimeException {
		throw new SpellRuntimeException(SpellRuntimeException.MEMORY_OUT_OF_BOUNDS);
	}

	@Override
	public int getTime(ItemStack stack) {
		// MPU has no clock
		return 0;
	}

	@Override
	public void incrementTime(ItemStack stack) {}

	@Override
	public int getSpellColor(ItemStack stack) {
		return ICADColorizer.DEFAULT_SPELL_COLOR;
	}

	@Override
	public boolean craft(ItemStack cad, PlayerEntity entity, PieceCraftingTrick trick) {
		// MPUs can't craft for whatever reason
		return false;
	}
	
}
