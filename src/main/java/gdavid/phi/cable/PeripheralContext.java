package gdavid.phi.cable;

import gdavid.phi.block.tile.MPUTile.MPUCaster;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.SpellContext;

public interface PeripheralContext {

	static PeripheralContext get(SpellContext context) {
		if (context.caster instanceof MPUCaster mpu) return new MPUPeripheralContext(mpu.level, mpu.blockPosition());
		return new PlayerPeripheralContext(context.caster);
	}
	
	<T extends ICableConnected> T getPeripheral(Class<T> clazz, Vector3 dir);

}
