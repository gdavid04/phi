package gdavid.phi.item;

import java.util.List;

import gdavid.phi.Phi;
import gdavid.phi.capability.MagazineSocketable;
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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.AssembleCADEvent;
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
		super(new Properties()
			.maxStackSize(1)
			.group(ItemGroup.MISC)); // TODO Phi creative tab
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
	public void addInformation(ItemStack item, World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("item." + Phi.modId + "." + id + ".desc"));
		tooltip.add(new TranslationTextComponent("item." + Phi.modId + ".spell_magazine.desc"));
		tooltip.add(new StringTextComponent(" ").append(new TranslationTextComponent(EnumCADStat.BANDWIDTH.getName()).mergeStyle(TextFormatting.AQUA)).appendString(": " + bandwidth));
		tooltip.add(new StringTextComponent(" ").append(new TranslationTextComponent(EnumCADStat.SOCKETS.getName()).mergeStyle(TextFormatting.AQUA)).appendString(": " + sockets));
		tooltip.add(new StringTextComponent(" ").append(new TranslationTextComponent(EnumCADStat.SAVED_VECTORS.getName()).mergeStyle(TextFormatting.AQUA)).appendString(": " + vectors));
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack item, CompoundNBT nbt) {
		return new MagazineSocketable(item, sockets);
	}
	
	@SubscribeEvent
	public static void AssembleCAD(AssembleCADEvent e) {
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
		ICAD cadItem = (ICAD) cad.getItem();
		ItemStack socket = cadItem.getComponentInSlot(cad, EnumCADComponent.SOCKET);
		if (!isCompatible(item, socket)) {
			return ActionResult.resultFail(item);
		}
		if (!world.isRemote) {
			swap(item, cad);
		}
		return ActionResult.resultConsume(item);
	}
	
	public boolean isCompatible(ItemStack item, ItemStack socket) {
		return item.getItem() == socket.getItem();
	}
	
	public void swap(ItemStack item, ItemStack cad) {
		ISocketable socket = ISocketable.socketable(cad);
		ISocketable contents = ISocketable.socketable(item);
		for (int i = 0; i < sockets; i++) {
			ItemStack bullet = socket.getBulletInSocket(i);
			socket.setBulletInSocket(i, contents.getBulletInSocket(i));
			contents.setBulletInSocket(i, bullet);
		}
		ICAD cadItem = (ICAD) cad.getItem();
		for (int i = 0; i < vectors; i++) {
			Vector3 vec = Vector3.zero;
			try {
				vec = cadItem.getStoredVector(cad, i);
				cadItem.setStoredVector(cad, i, getStoredVector(item, i));
			} catch (SpellRuntimeException e) {}
			setStoredVector(item, i, vec);
		}
	}
	
	public Vector3 getStoredVector(ItemStack item, int slot) {
		CompoundNBT nbt = item.getOrCreateTag();
		return new Vector3(
			nbt.getDouble(tagVector + slot + "_x"),
			nbt.getDouble(tagVector + slot + "_y"),
			nbt.getDouble(tagVector + slot + "_z")
		);
	}
	
	public void setStoredVector(ItemStack item, int slot, Vector3 vec) {
		CompoundNBT nbt = item.getOrCreateTag();
		nbt.putDouble(tagVector + slot + "_x", vec.x);
		nbt.putDouble(tagVector + slot + "_y", vec.y);
		nbt.putDouble(tagVector + slot + "_z", vec.z);
	}
	
}
