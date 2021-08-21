package gdavid.phi.item;

import java.util.List;

import gdavid.phi.Phi;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADComponent;

@EventBusSubscriber
public class BloodConverterItem extends Item implements ICADComponent {
	
	public final String id;
	
	public float damageMultiplier;
	
	public BloodConverterItem(String id, float damageMultiplier) {
		super(new Properties().maxStackSize(1).group(ItemGroup.MISC)); // TODO Phi creative tab
		setRegistryName(id);
		this.id = id;
		this.damageMultiplier = damageMultiplier;
	}
	
	@Override
	public EnumCADComponent getComponentType(ItemStack stack) {
		return EnumCADComponent.BATTERY;
	}
	
	@Override
	public int getCADStatValue(ItemStack stack, EnumCADStat stat) {
		return 0;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack item, World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("item." + Phi.modId + "." + id + ".desc"));
		tooltip.add(new StringTextComponent(" ")
				.append(new TranslationTextComponent(Phi.modId + ".cadstat.overflow_damage").mergeStyle(TextFormatting.AQUA))
				.appendString(": ")
				.append(new TranslationTextComponent("-" + 100 * (1 - damageMultiplier) + "%").mergeStyle(TextFormatting.GREEN)));
	}
	
	@SubscribeEvent
	public static void overflowDamage(LivingHurtEvent event) {
		if (!event.getSource().getDamageType().equals("psi-overload")) return;
		ItemStack item = PsiAPI.getPlayerCAD((PlayerEntity) event.getEntity());
		ICAD cad = (ICAD) item.getItem();
		Item battery = cad.getComponentInSlot(item, EnumCADComponent.BATTERY).getItem();
		if (battery instanceof BloodConverterItem) {
			event.setAmount(event.getAmount() * ((BloodConverterItem) battery).damageMultiplier);
		}
	}
	
}
