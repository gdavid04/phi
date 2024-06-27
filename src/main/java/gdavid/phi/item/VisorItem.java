package gdavid.phi.item;

import gdavid.phi.Phi;
import gdavid.phi.capability.SimpleSpellAcceptor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.psi.api.PsiAPI;

import java.util.List;

public class VisorItem extends ArmorItem {
	
	public final String id;
	
	public VisorItem(String id) {
		super(PsiAPI.PSIMETAL_ARMOR_MATERIAL, EquipmentSlot.HEAD, new Properties().rarity(Rarity.UNCOMMON).tab(CreativeModeTab.TAB_MISC)); // TODO Phi creative tab
		this.id = id;
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack item, CompoundTag nbt) {
		return new SimpleSpellAcceptor(item);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack item, Level world, List<Component> tooltip, TooltipFlag advanced) {
		tooltip.add(Component.translatable("item." + Phi.modId + "." + id + ".desc"));
	}
	
	@Override
	public boolean canBeDepleted() {
		return false;
	}
	
}
