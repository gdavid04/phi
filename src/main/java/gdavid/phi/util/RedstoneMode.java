package gdavid.phi.util;

import gdavid.phi.Phi;
import net.minecraft.resources.ResourceLocation;

public enum RedstoneMode {
	
	always, enable, disable, pulse;
	
	public static final ResourceLocation texture = new ResourceLocation(Phi.modId, "textures/gui/redstone_mode.png");
	
	public boolean isActive(boolean prev, boolean cur) {
		if (this == always) return true;
		if (this == pulse) return cur && !prev;
		return cur == (this == enable);
	}
	
}
