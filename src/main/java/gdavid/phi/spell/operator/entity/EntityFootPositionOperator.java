package gdavid.phi.spell.operator.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceOperator;

public class EntityFootPositionOperator extends PieceOperator {
	
	SpellParam<Entity> target;
	
	public EntityFootPositionOperator(Spell spell) {
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
		return Vector3.fromEntity(e);
	}
	
	@Override
	public String getSortingName() {
		return new TranslationTextComponent(PsiAPI.MOD_ID + ".spellpiece.operator_entity_position").getString() +
				"/" + super.getSortingName();
	}
	
}
