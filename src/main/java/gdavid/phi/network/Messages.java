package gdavid.phi.network;

import gdavid.phi.Phi;
import java.util.function.Function;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

@EventBusSubscriber(bus = Bus.MOD)
public class Messages {
	
	static final String version = "1";
	public static final SimpleChannel channel = NetworkRegistry
			.newSimpleChannel(new ResourceLocation(Phi.modId, "main"), () -> version, version::equals, version::equals);
	
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		register(AccelerationMessage.class);
		register(ProgramTransferMessage.class);
		register(ProgramTransferSlotMessage.class);
		register(CADScanMessage.class);
		register(GravityMessage.class);
	}
	
	static int id = 0;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static void register(Class<? extends Message> clazz) {
		channel.messageBuilder(clazz, id++).encoder(Message::encode).consumer(Message::receive)
				.decoder((Function) buf -> {
					try {
						return clazz.getConstructor(FriendlyByteBuf.class).newInstance(buf);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}).add();
	}
	
	public static void send(Message message, Player player) {
		channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), message);
	}
	
}
