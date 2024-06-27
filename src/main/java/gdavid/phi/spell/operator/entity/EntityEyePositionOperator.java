package gdavid.phi.spell.operator.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceOperator;

public class EntityEyePositionOperator extends PieceOperator {
	
	SpellParam<Entity> target;
	
	public EntityEyePositionOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamEntity(SpellParam.GENERIC_NAME_TARGET, SpellParam.YELLOW, false, false));
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Vector3.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Entity e = getNonnullParamValue(context, target);
		return Vector3.fromEntity(e).add(0, e.getEyeHeight(), 0);
	}
	
	@Override
	public String getSortingName() {
		return Component.translatable(PsiAPI.MOD_ID + ".spellpiece.operator_entity_position").getString() + "/"
				+ super.getSortingName();
	}
	
}
