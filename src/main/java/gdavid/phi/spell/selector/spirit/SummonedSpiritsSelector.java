package gdavid.phi.spell.selector.spirit;

import java.util.ArrayList;
import java.util.List;

import gdavid.phi.item.SpiritSummoningTalismanItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class SummonedSpiritsSelector extends PieceSelector {
	
	public SummonedSpiritsSelector(Spell spell) {
		super(spell);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (!(context.caster.world instanceof ServerWorld)) return EntityListWrapper.EMPTY;
		List<Entity> list = new ArrayList<>();
		for (int i = 0; i < context.caster.inventory.getSizeInventory(); i++) {
			ItemStack item = context.caster.inventory.getStackInSlot(i);
			if (!(item.getItem() instanceof SpiritSummoningTalismanItem)) continue;
			list.add(((SpiritSummoningTalismanItem) item.getItem()).getSpirit(item, (ServerWorld) context.caster.world));
		}
		return EntityListWrapper.make(list);
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return EntityListWrapper.class;
	}
	
}
