package gdavid.phi.api.util;

import java.util.Stack;

import gdavid.phi.api.CustomForkData;
import vazkii.psi.api.spell.CompiledSpell.Action;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellGrid;

public class ContextHelper {
	
	@SuppressWarnings("unchecked")
	public static SpellContext fork(SpellContext original) {
		SpellContext context = new SpellContext();
		context.caster = original.caster;
		context.focalPoint = original.focalPoint;
		context.cspell = original.cspell;
		context.loopcastIndex = original.loopcastIndex;
		context.castFrom = original.castFrom;
		context.tool = original.tool;
		context.positionBroken = original.positionBroken;
		context.attackedEntity = original.attackedEntity;
		context.attackingEntity = original.attackingEntity;
		context.damageTaken = original.damageTaken;
		context.targetSlot = original.targetSlot;
		context.shiftTargetSlot = original.shiftTargetSlot;
		context.customTargetSlot = original.customTargetSlot;
		context.customData.putAll(original.customData);
		context.customData.replaceAll((k, v) -> (v instanceof CustomForkData) ? ((CustomForkData) v).fork() : v);
		for (int x = 0; x < SpellGrid.GRID_SIZE; x++) {
			for (int y = 0; y < SpellGrid.GRID_SIZE; y++) {
				Object o = original.evaluatedObjects[x][y];
				context.evaluatedObjects[x][y] = (o instanceof CustomForkData) ? ((CustomForkData) o).fork() : o;
			}
		}
		// assume no Action implements CustomForkData for now
		context.actions = (Stack<Action>) original.actions.clone();
		// stopped and delay are not copied
		return context;
	}
	
}
