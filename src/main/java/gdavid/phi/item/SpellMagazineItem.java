package gdavid.phi.item;

import gdavid.phi.Phi;
import gdavid.phi.capability.MagazineSocketable;
import java.util.List;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.CADTakeEvent;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADComponent;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.SpellRuntimeException;

import net.minecraft.world.item.Item.Properties;

@EventBusSubscriber
public class SpellMagazineItem extends Item implements ICADComponent {
	
	public static final String tagSlot = "slot";
	public static final String tagVector = "vector";
	
	public final String id;
	
	public int bandwidth, sockets, vectors;
	
	public SpellMagazineItem(String id, int sockets, int bandwidth, int vectors) {
		super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
		this.id = id;
		this.bandwidth = bandwidth;
		this.sockets = sockets;
		this.vectors = vectors;
	}
	
	@Override
	public EnumCADComponent getComponentType(ItemStack stack) {
		return EnumCADComponent.SOCKET;
	}
	
	@Override
	public int getCADStatValue(ItemStack stack, EnumCADStat stat) {
		if (stat == EnumCADStat.BANDWIDTH) return bandwidth;
		if (stat == EnumCADStat.SOCKETS) return sockets;
		if (stat == EnumCADStat.SAVED_VECTORS) return vectors;
		return 0;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack item, Level world, List<Component> tooltip, TooltipFlag advanced) {
		ISocketable socket = ISocketable.socketable(item);
		tooltip.add(Component.translatable("item." + Phi.modId + "." + id + ".desc"));
		tooltip.add(Component.translatable("item." + Phi.modId + ".spell_magazine.desc"));
		tooltip.add(Component.literal(" ")
				.append(Component.translatable(EnumCADStat.BANDWIDTH.getName()).withStyle(ChatFormatting.AQUA))
				.append(": " + bandwidth));
		tooltip.add(Component.literal(" ")
				.append(Component.translatable(EnumCADStat.SOCKETS.getName()).withStyle(ChatFormatting.AQUA))
				.append(": " + sockets));
		for (int i = 0; i < sockets; i++) {
			ItemStack bullet = socket.getBulletInSocket(i);
			if (bullet.isEmpty()) tooltip.add(Component.literal(" - [Empty]").withStyle(ChatFormatting.GRAY));
			else tooltip.add(Component.literal(" - ").withStyle(ChatFormatting.GRAY)
					.append(socket.getBulletInSocket(i).getHoverName()));
		}
		tooltip.add(Component.literal(" ").append(
				Component.translatable(EnumCADStat.SAVED_VECTORS.getName()).withStyle(ChatFormatting.AQUA))
				.append(": " + vectors));
		for (int i = 0; i < vectors; i++) {
			Vector3 vector = getStoredVector(item, i);
			if (vector.isZero()) continue;
			tooltip.add(Component.literal(" " + (i + 1) + ": [").withStyle(ChatFormatting.GRAY)
					.append(Component.literal(Double.toString(vector.x)).withStyle(ChatFormatting.RED))
					.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
					.append(Component.literal(Double.toString(vector.y)).withStyle(ChatFormatting.GREEN))
					.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
					.append(Component.literal(Double.toString(vector.z)).withStyle(ChatFormatting.BLUE))
					.append(Component.literal("]").withStyle(ChatFormatting.GRAY)));
		}
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack item, CompoundTag nbt) {
		return new MagazineSocketable(item, sockets);
	}
	
	@SubscribeEvent
	public static void TakeCAD(CADTakeEvent e) {
		// Assembling a CAD with a magazine transfers bullets and vectors to the CAD
		ItemStack socket = e.getAssembler().getStackForComponent(EnumCADComponent.SOCKET);
		if (socket.getItem() instanceof SpellMagazineItem) {
			((SpellMagazineItem) socket.getItem()).insertMag(socket.copy(), e.getCad());
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);
		ItemStack cad = PsiAPI.getPlayerCAD(player);
		ItemStack old = ejectMag(cad);
		if (old.isEmpty()) return InteractionResultHolder.fail(item);
		if (!world.isClientSide) {
			insertMag(item, cad);
		}
		return InteractionResultHolder.consume(old);
	}
	
	/**
	 * Creates a copy of the magazine in the CAD and copies all socket data from the
	 * CAD to the magazine
	 */
	public static ItemStack ejectMag(ItemStack cad) {
		if (!(cad.getItem() instanceof ICAD) || !ISocketable.isSocketable(cad)) return ItemStack.EMPTY;
		ICAD cadItem = (ICAD) cad.getItem();
		ItemStack socketStack = cadItem.getComponentInSlot(cad, EnumCADComponent.SOCKET).copy();
		if (!(socketStack.getItem() instanceof SpellMagazineItem)) return ItemStack.EMPTY;
		((SpellMagazineItem) socketStack.getItem()).fromCad(cad, socketStack);
		return socketStack;
	}
	
	/**
	 * Copies all socket data (bullets and vectors) from the CAD to the magazine
	 */
	public void fromCad(ItemStack cad, ItemStack mag) {
		if (!(cad.getItem() instanceof ICAD) || !ISocketable.isSocketable(cad)) return;
		ICAD cadItem = (ICAD) cad.getItem();
		ISocketable socket = ISocketable.socketable(cad);
		ISocketable contents = ISocketable.socketable(mag);
		for (int i = 0; i < sockets; i++) {
			contents.setBulletInSocket(i, socket.getBulletInSocket(i));
		}
		for (int i = 0; i < vectors; i++) {
			try {
				setStoredVector(mag, i, cadItem.getStoredVector(cad, i));
			} catch (SpellRuntimeException e) {
			}
		}
	}
	
	public void insertMag(ItemStack mag, ItemStack cad) {
		if (!(cad.getItem() instanceof ICAD) || !ISocketable.isSocketable(cad)) return;
		ICAD cadItem = (ICAD) cad.getItem();
		ISocketable socket = ISocketable.socketable(cad);
		// clear extra slots to prevent duping when swapping to a smaller magazine
		for (int i = sockets; i < ISocketable.MAX_ASSEMBLER_SLOTS; i++) {
			socket.setBulletInSocket(i, ItemStack.EMPTY);
		}
		// set component to change stats and allow safe writing of socket data to the
		// CAD
		cadItem.setCADComponent(cad, mag);
		ISocketable contents = ISocketable.socketable(mag);
		for (int i = 0; i < sockets; i++) {
			socket.setBulletInSocket(i, contents.getBulletInSocket(i));
		}
		for (int i = 0; i < vectors; i++) {
			try {
				cadItem.setStoredVector(cad, i, getStoredVector(mag, i));
			} catch (SpellRuntimeException e) {
			}
		}
		// remove duplicate socket data
		stripCadData(mag);
		// set component again to reflect changes to nbt
		cadItem.setCADComponent(cad, mag);
	}
	
	public void stripCadData(ItemStack item) {
		item.removeTagKey(tagVector);
		item.removeTagKey(tagSlot);
	}
	
	public Vector3 getStoredVector(ItemStack item, int slot) {
		CompoundTag nbt = item.getOrCreateTagElement(tagVector);
		return new Vector3(nbt.getDouble(slot + "_x"), nbt.getDouble(slot + "_y"), nbt.getDouble(slot + "_z"));
	}
	
	public void setStoredVector(ItemStack item, int slot, Vector3 vec) {
		CompoundTag nbt = item.getOrCreateTagElement(tagVector);
		nbt.putDouble(slot + "_x", vec.x);
		nbt.putDouble(slot + "_y", vec.y);
		nbt.putDouble(slot + "_z", vec.z);
	}
	
}
