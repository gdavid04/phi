package gdavid.phi.api.param;

import vazkii.psi.api.spell.param.ParamSpecific;

public class TextParam extends ParamSpecific<String> {
	
	public TextParam(String name, int color, boolean canDisable, boolean constant) {
		super(name, color, canDisable, constant);
	}
	
	@Override
	protected Class<String> getRequiredType() {
		return String.class;
	}
	
}
