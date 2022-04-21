package gdavid.phi.network;

import java.util.function.Function;

import gdavid.phi.Phi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@EventBusSubscriber(bus = Bus.MOD)
public class Messages {
	
	static final String version = "1";
	public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Phi.modId, "main"), () -> version, version::equals, version::equals);
	
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		register(AccelerationMessage.class);
		register(ProgramTransferMessage.class);
	}
	
	static int id = 0;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static void register(Class<? extends Message> clazz) {
		channel.messageBuilder(clazz, id++)
				.encoder(Message::encode).consumer(Message::receive)
				.decoder((Function) buf -> {
					try {
						return clazz.getConstructor(PacketBuffer.class).newInstance(buf);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}).add();
	}
	
	public static void send(Message message, PlayerEntity player) {
		channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
	}
	
}
