package gdavid.phi.spell;

import com.google.common.base.CaseFormat;
import gdavid.phi.Phi;
import vazkii.psi.api.PsiAPI;

public enum Param {
	
	speed("_speed"), frequency, from, to, fromTo, toFrom, condition, positive, negative, div, mod, digit, target1,
	target2, pre("prefix"), text, text1, text2, text3, text4, before, after, at;
	
	public static final String prefix = Phi.modId + ".spellparam.";
	public static final String psiPrefix = PsiAPI.MOD_ID + ".spellparam.";
	
	public final String name;
	
	Param() {
		name = prefix + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name());
	}
	
	Param(String name) {
		if (name.startsWith("_")) {
			this.name = psiPrefix + name.substring(1);
		} else {
			this.name = prefix + name;
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
