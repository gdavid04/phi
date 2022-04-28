package gdavid.phi.network;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public interface Message {
	
	// constructor(PacketBuffer buf);
	
	void encode(PacketBuffer buf);
	
	boolean receive(Supplier<NetworkEvent.Context> context);
	
}
