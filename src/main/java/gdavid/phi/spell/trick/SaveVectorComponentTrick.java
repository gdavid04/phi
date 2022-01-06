package gdavid.phi.spell.trick;

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
import vazkii.psi.api.spell.piece.PieceTrick;

public class SaveVectorComponentTrick extends PieceTrick {
	
	public static final String vectorLocked = "psi:SlotLocked";
	
	SpellParam<Number> target, number;
	
	public SaveVectorComponentTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamNumber(SpellParam.GENERIC_NAME_TARGET, SpellParam.BLUE, false, true));
		addParam(number = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.RED, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
		double lastTarget = ParamHelper.positive(this, target);
		meta.addStat(EnumSpellStat.POTENCY, (int) (lastTarget * 2));
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		int targetVal = getParamValue(context, target).intValue() - 1;
		int n = targetVal / 3, c = targetVal % 3;
		ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
		if (!(cad.getItem() instanceof ICAD)) {
			throw new SpellRuntimeException(SpellRuntimeException.NO_CAD);
		}
		Vector3 vec = ((ICAD) cad.getItem()).getStoredVector(cad, n).copy();
		setComponent(vec, c, getNonnullParamValue(context, number).doubleValue());
		((ICAD) cad.getItem()).setStoredVector(cad, n, vec);
		lock(context, n, c);
		return null;
	}
	
	public static void lock(SpellContext context, int n, int c) {
		if (context.customData.containsKey(vectorLocked + n)) {
			int lock = (int) context.customData.get(vectorLocked + n);
			if (lock != 0) {
				context.customData.put(vectorLocked + n, lock | (1 << c));
			}
		} else {
			context.customData.put(vectorLocked + n, 1 << c);
		}
	}
	
	public static Vector3 setComponent(Vector3 vec, int c, double num) {
		if (c == 0) vec.x = num;
		else if (c == 1) vec.y = num;
		else vec.z = num;
		return vec;
	}
	
}
