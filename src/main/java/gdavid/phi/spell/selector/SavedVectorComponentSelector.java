package gdavid.phi.spell.selector;

import gdavid.phi.spell.trick.SaveVectorComponentTrick;
import gdavid.phi.util.ParamHelper;
import net.minecraft.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceSelector;

public class SavedVectorComponentSelector extends PieceSelector {
	
	SpellParam<Number> target;
	
	public SavedVectorComponentSelector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		double lastTarget = ParamHelper.positive(this, target);
		meta.addStat(EnumSpellStat.POTENCY, (int) (lastTarget * 2));
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamNumber(SpellParam.GENERIC_NAME_TARGET, SpellParam.BLUE, false, true));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		int targetVal = getParamValue(context, target).intValue() - 1;
		int n = targetVal / 3, c = targetVal % 3;
		if (isLocked(context, n, c)) {
			throw new SpellRuntimeException(SpellRuntimeException.LOCKED_MEMORY);
		}
		ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
		if (!(cad.getItem() instanceof ICAD)) {
			throw new SpellRuntimeException(SpellRuntimeException.NO_CAD);
		}
		return getComponent(((ICAD) cad.getItem()).getStoredVector(cad, n), c);
	}
	
	public static boolean isLocked(SpellContext context, int n, int c) {
		if (!context.customData.containsKey(SaveVectorComponentTrick.vectorLocked + n)) return false;
		int val = (int) context.customData.get(SaveVectorComponentTrick.vectorLocked + n);
		return val == 0 || (val & (1 << c)) != 0;
	}
	
	public static double getComponent(Vector3 v, int c) {
		if (c == 0) return v.x;
		if (c == 1) return v.y;
		return v.z;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
}
