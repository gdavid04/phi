package gdavid.phi.item;

import gdavid.phi.Phi;
import gdavid.phi.capability.MagazineSocketable;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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

@EventBusSubscriber
public class SpellMagazineItem extends Item implements ICADComponent {
	
	public static final String tagSlot = "slot";
	public static final String tagVector = "vector";
	
	public final String id;
	
	public int bandwidth, sockets, vectors;
	
	public SpellMagazineItem(String id, int sockets, int bandwidth, int vectors) {
		super(new Properties().maxStackSize(1).group(ItemGroup.MISC)); // TODO Phi creative tab
		setRegistryName(id);
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
	public void addInformation(ItemStack item, World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("item." + Phi.modId + "." + id + ".desc"));
		tooltip.add(new TranslationTextComponent("item." + Phi.modId + ".spell_magazine.desc"));
		tooltip.add(new StringTextComponent(" ")
				.append(new TranslationTextComponent(EnumCADStat.BANDWIDTH.getName()).mergeStyle(TextFormatting.AQUA))
				.appendString(": " + bandwidth));
		tooltip.add(new StringTextComponent(" ")
				.append(new TranslationTextComponent(EnumCADStat.SOCKETS.getName()).mergeStyle(TextFormatting.AQUA))
				.appendString(": " + sockets));
		tooltip.add(new StringTextComponent(" ").append(
				new TranslationTextComponent(EnumCADStat.SAVED_VECTORS.getName()).mergeStyle(TextFormatting.AQUA))
				.appendString(": " + vectors));
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack item, CompoundNBT nbt) {
		return new MagazineSocketable(item, sockets);
	}
	
	@SubscribeEvent
	public static void TakeCAD(CADTakeEvent e) {
		// Assembling a CAD with a magazine transfers bullets and vectors to the CAD
		ItemStack socket = e.getAssembler().getStackForComponent(EnumCADComponent.SOCKET);
		if (socket.getItem() instanceof SpellMagazineItem) {
			((SpellMagazineItem) socket.getItem()).insertMag(socket, e.getCad());
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack item = player.getHeldItem(hand);
		ItemStack cad = PsiAPI.getPlayerCAD(player);
		ItemStack old = ejectMag(cad);
		if (old.isEmpty()) return ActionResult.resultFail(item);
		if (!world.isRemote) {
			insertMag(item, cad);
		}
		return ActionResult.resultConsume(old);
	}
	
	/**
	 * Creates a copy of the magazine in the CAD and copies all socket data from the CAD to the magazine
	 */
	public static ItemStack ejectMag(ItemStack cad) {
		if (cad == null || !(cad.getItem() instanceof ICAD) || !ISocketable.isSocketable(cad)) return ItemStack.EMPTY;
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
		if (cad == null || !(cad.getItem() instanceof ICAD) || !ISocketable.isSocketable(cad)) return;
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
		if (cad == null || !(cad.getItem() instanceof ICAD) || !ISocketable.isSocketable(cad)) return;
		ICAD cadItem = (ICAD) cad.getItem();
		// set component to change stats and allow safe writing of socket data to the CAD
		cadItem.setCADComponent(cad, mag);
		ISocketable socket = ISocketable.socketable(cad);
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
		item.removeChildTag(tagVector);
		item.removeChildTag(tagSlot);
	}
	
	public Vector3 getStoredVector(ItemStack item, int slot) {
		CompoundNBT nbt = item.getOrCreateChildTag(tagVector);
		return new Vector3(nbt.getDouble(slot + "_x"), nbt.getDouble(slot + "_y"), nbt.getDouble(slot + "_z"));
	}
	
	public void setStoredVector(ItemStack item, int slot, Vector3 vec) {
		CompoundNBT nbt = item.getOrCreateChildTag(tagVector);
		nbt.putDouble(slot + "_x", vec.x);
		nbt.putDouble(slot + "_y", vec.y);
		nbt.putDouble(slot + "_z", vec.z);
	}
	
}
