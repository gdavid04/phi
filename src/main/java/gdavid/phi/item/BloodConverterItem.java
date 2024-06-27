package gdavid.phi.item;

import gdavid.phi.Phi;
import java.util.List;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
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

import net.minecraft.world.item.Item.Properties;

@EventBusSubscriber
public class BloodConverterItem extends Item implements ICADComponent {
	
	public final String id;
	
	public float damageMultiplier;
	
	public BloodConverterItem(String id, float damageMultiplier) {
		super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)); // TODO Phi creative tab
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
	public void appendHoverText(ItemStack item, Level world, List<Component> tooltip, TooltipFlag advanced) {
		tooltip.add(Component.translatable("item." + Phi.modId + "." + id + ".desc"));
		tooltip.add(Component.literal(" ")
				.append(Component.translatable(Phi.modId + ".cadstat.overflow_damage")
						.withStyle(ChatFormatting.AQUA))
				.append(": ").append(Component.translatable("-" + 100 * (1 - damageMultiplier) + "%")
						.withStyle(ChatFormatting.GREEN)));
	}
	
	@SubscribeEvent
	public static void overflowDamage(LivingHurtEvent event) {
		if (!event.getSource().getMsgId().equals("psi-overload")) return;
		ItemStack item = PsiAPI.getPlayerCAD((Player) event.getEntity());
		ICAD cad = (ICAD) item.getItem();
		Item battery = cad.getComponentInSlot(item, EnumCADComponent.BATTERY).getItem();
		if (battery instanceof BloodConverterItem) {
			event.setAmount(event.getAmount() * ((BloodConverterItem) battery).damageMultiplier);
		}
	}
	
}
