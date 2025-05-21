package gdavid.phi.spell.selector.spirit;

import gdavid.phi.item.SpiritSummoningTalismanItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

import java.util.ArrayList;
import java.util.List;

public class SummonedSpiritsSelector extends PieceSelector {
	
	public SummonedSpiritsSelector(Spell spell) {
		super(spell);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (!(context.caster.level() instanceof ServerLevel)) return EntityListWrapper.EMPTY;
		List<Entity> list = new ArrayList<>();
		for (int i = 0; i < context.caster.getInventory().getContainerSize(); i++) {
			ItemStack item = context.caster.getInventory().getItem(i);
			if (!(item.getItem() instanceof SpiritSummoningTalismanItem)) continue;
			list.add(
					((SpiritSummoningTalismanItem) item.getItem()).getSpirit(item, (ServerLevel) context.caster.level()));
		}
		return EntityListWrapper.make(list);
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return EntityListWrapper.class;
	}
	
}
