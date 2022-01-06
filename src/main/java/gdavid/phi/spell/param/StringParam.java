package gdavid.phi.spell.param;

import vazkii.psi.api.spell.param.ParamSpecific;

public class StringParam extends ParamSpecific<String> {
	
	public StringParam(String name, int color, boolean canDisable) {
		super(name, color, canDisable, false);
	}
	
	@Override
	protected Class<String> getRequiredType() {
		return String.class;
	}
	
}
