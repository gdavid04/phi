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
	
	public static final String tagSlot = "slot_";
	public static final String tagVector = "vector_";
	
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
			((SpellMagazineItem) socket.getItem()).swap(socket, e.getCad());
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack item = player.getHeldItem(hand);
		ItemStack cad = PsiAPI.getPlayerCAD(player);
		if (!isCADCompatible(item, cad)) {
			return ActionResult.resultFail(item);
		}
		if (!world.isRemote) {
			swap(item, cad);
		}
		return ActionResult.resultConsume(item);
	}
	
	public boolean isCADCompatible(ItemStack item, ItemStack cad) {
		if (cad == null || !(cad.getItem() instanceof ICAD) || !ISocketable.isSocketable(cad)) {
			return false;
		}
		return isSocketCompatible(item, ((ICAD) cad.getItem()).getComponentInSlot(cad, EnumCADComponent.SOCKET));
	}
	
	public boolean isSocketCompatible(ItemStack item, ItemStack socket) {
		return item.getItem() == socket.getItem();
	}
	
	public void swap(ItemStack item, ItemStack cad) {
		if (cad == null || !(cad.getItem() instanceof ICAD) || !ISocketable.isSocketable(cad)) {
			return;
		}
		ICAD cadItem = (ICAD) cad.getItem();
		ItemStack socketStack = cadItem.getComponentInSlot(cad, EnumCADComponent.SOCKET);
		ITextComponent name = item.getDisplayName();
		item.setDisplayName(socketStack.getDisplayName());
		socketStack.setDisplayName(name);
		ISocketable socket = ISocketable.socketable(cad);
		ISocketable contents = ISocketable.socketable(item);
		for (int i = 0; i < sockets; i++) {
			ItemStack bullet = socket.getBulletInSocket(i);
			socket.setBulletInSocket(i, contents.getBulletInSocket(i));
			contents.setBulletInSocket(i, bullet);
		}
		for (int i = 0; i < vectors; i++) {
			try {
				Vector3 vec = cadItem.getStoredVector(cad, i);
				cadItem.setStoredVector(cad, i, getStoredVector(item, i));
				setStoredVector(item, i, vec);
			} catch (SpellRuntimeException e) {
			}
		}
	}
	
	public Vector3 getStoredVector(ItemStack item, int slot) {
		CompoundNBT nbt = item.getOrCreateTag();
		return new Vector3(nbt.getDouble(tagVector + slot + "_x"), nbt.getDouble(tagVector + slot + "_y"),
				nbt.getDouble(tagVector + slot + "_z"));
	}
	
	public void setStoredVector(ItemStack item, int slot, Vector3 vec) {
		CompoundNBT nbt = item.getOrCreateTag();
		nbt.putDouble(tagVector + slot + "_x", vec.x);
		nbt.putDouble(tagVector + slot + "_y", vec.y);
		nbt.putDouble(tagVector + slot + "_z", vec.z);
	}
	
}
