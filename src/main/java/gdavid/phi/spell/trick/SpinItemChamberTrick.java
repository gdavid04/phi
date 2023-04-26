package gdavid.phi.spell.trick;

import gdavid.phi.spell.Errors;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;

public class SpinItemChamberTrick extends PieceTrick {
	
	SpellParam<Number> slot;
	SpellParam<Number> direction;
	SpellParam<Number> position;
	
	public SpinItemChamberTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(slot = new ParamNumber(SpellParam.GENERIC_NAME_SLOT, SpellParam.BLUE, false, false));
		addParam(direction = new ParamNumber(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, true, false));
		addParam(position = new ParamNumber(SpellParam.GENERIC_NAME_POSITION, SpellParam.RED, true, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		meta.addStat(EnumSpellStat.POTENCY, 10);
		if (!(paramSides.get(direction).isEnabled() || paramSides.get(position).isEnabled())) {
			Errors.compile(SpellCompilationException.UNSET_PARAM);
		} else if (paramSides.get(direction).isEnabled() && paramSides.get(position).isEnabled()) {
			Errors.compile("psi.spellerror.exclusiveparams");
		}
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		int slotVal = getNonnullParamValue(context, slot).intValue();
		ItemStack stack = getItemInSlot(context, slotVal);
		if (!ISocketable.isSocketable(stack)) Errors.invalidTarget.runtime();
		ISocketable socketable = ISocketable.socketable(stack);
		Number directionVal = getParamValue(context, direction);
		int toSelect = socketable.getSelectedSlot();
		if (directionVal != null) {
			if (directionVal.doubleValue() > 0) toSelect++;
			else if (directionVal.doubleValue() < 0) toSelect--;
			else return null;
		} else {
			toSelect = getNonnullParamValue(context, position).intValue() - 1;
		}
		int slots = socketable.getLastSlot() + 1;
		toSelect %= slots;
		if (toSelect < 0) toSelect += slots;
		socketable.setSelectedSlot(toSelect);
		PlayerData data = PlayerDataHandler.get(context.caster);
		if (stack.getItem() instanceof ICAD && context.castFrom == data.loopcastHand && isSlot(context, slotVal, context.castFrom)) {
			data.lastTickLoopcastStack = stack.copy();
		}
		return null;
	}
	
	private ItemStack getItemInSlot(SpellContext context, int slot) throws SpellRuntimeException {
		if (slot > 0) return context.caster.inventory.getStackInSlot(slot % 36);
		if (-slot < EquipmentSlotType.values().length) return context.caster.getItemStackFromSlot(EquipmentSlotType.values()[-slot]);
		Errors.invalidTarget.runtime();
		return ItemStack.EMPTY;
	}
	
	private boolean isSlot(SpellContext context, int slot, Hand hand) {
		return (-slot == EquipmentSlotType.MAINHAND.getIndex() && hand == Hand.MAIN_HAND)
			|| (-slot == EquipmentSlotType.OFFHAND.getIndex() && hand == Hand.OFF_HAND)
			|| (slot > 0 && slot % 36 == context.caster.inventory.currentItem - 1);
	}
	
}
