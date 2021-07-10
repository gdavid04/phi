package gdavid.phi;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod(Phi.modId)
@EventBusSubscriber
public class Phi {
	
	public static final String modId = "phi";
	
	@SubscribeEvent
	public static void overflowDamage(LivingHurtEvent event) {
		if (!event.getSource().getDamageType().equals("psi-overload")) return;
		float multiplier = 1;
		EffectInstance effect = event.getEntityLiving().getActivePotionEffect(Effects.RESISTANCE);
		if (effect != null) multiplier -= (effect.getAmplifier() + 1) * 0.2f;
		if (multiplier < 0) multiplier = 0;
		event.setAmount(event.getAmount() * multiplier);
	}
	
}
