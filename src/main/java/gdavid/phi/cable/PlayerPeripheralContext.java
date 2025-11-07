package gdavid.phi.cable;

import net.minecraft.world.entity.player.Player;
import vazkii.psi.api.internal.Vector3;

public class PlayerPeripheralContext implements PeripheralContext {
	
	public final Player player;
	
	public PlayerPeripheralContext(Player player) {
		this.player = player;
	}
	
	@Override
	public <T extends ICableConnected> T getPeripheral(Class<T> clazz, Vector3 dir) {
		return null;
	}
	
}
