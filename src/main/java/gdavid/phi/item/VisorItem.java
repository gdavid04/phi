package gdavid.phi.item;

import java.util.List;

import gdavid.phi.Phi;
import gdavid.phi.capability.SimpleSpellAcceptor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.psi.api.PsiAPI;

public class VisorItem extends ArmorItem {
	
	public final String id;
	
	public VisorItem(String id) {
		super(PsiAPI.PSIMETAL_ARMOR_MATERIAL, EquipmentSlotType.HEAD, new Properties().rarity(Rarity.UNCOMMON).group(ItemGroup.MISC)); // TODO Phi creative tab
		setRegistryName(id);
		this.id = id;
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack item, CompoundNBT nbt) {
		return new SimpleSpellAcceptor(item);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack item, World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("item." + Phi.modId + "." + id + ".desc"));
	}
	
	@Override
	public boolean isDamageable() {
		return false;
	}
	
}
