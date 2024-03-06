package gdavid.phi.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public interface Message {
	
	// constructor(PacketBuffer buf);
	
	void encode(FriendlyByteBuf buf);
	
	boolean receive(Supplier<Context> context);
	
}
