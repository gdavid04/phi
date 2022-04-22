package gdavid.phi.gui;

import java.util.Stack;

import gdavid.phi.Phi;
import gdavid.phi.item.VisorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.CompiledSpell.Action;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class VisorHUD {
	
	@SubscribeEvent
	@SuppressWarnings({ "resource", "unchecked" })
	public static void render(RenderGameOverlayEvent.Pre event) {
		if (event.getType() != ElementType.ALL) return;
		PlayerEntity player = Minecraft.getInstance().player;
		ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
		if (!(helmet.getItem() instanceof VisorItem)) return;
		ItemStack cad = PsiAPI.getPlayerCAD(player);
		if (cad == null) {
			renderMessage(I18n.format(helmet.getItem().getTranslationKey() + ".no_cad"));
			return;
		}
		Spell spell = ISpellAcceptor.acceptor(helmet).getSpell();
		if (spell == null) return;
		SpellContext ctx = new SpellContext().setPlayer(player).setSpell(spell);
		ctx.tool = helmet;
		if (!ctx.isValid()) {
			renderMessage(I18n.format(helmet.getItem().getTranslationKey() + ".invalid_spell"));
			return;
		}
		if (!ctx.cspell.metadata.evaluateAgainst(cad)) {
			renderMessage(I18n.format(helmet.getItem().getTranslationKey() + ".weak_cad"));
			return;
		}
		ctx.customData.put(Phi.modId + ":visor.window", event.getWindow());
		// TODO piece safety check
		ctx.actions = (Stack<Action>) ctx.cspell.actions.clone();
		try {
			// Bypass the non-client side check by calling execute directly
			ctx.cspell.execute(ctx);
			// TODO render
		} catch (SpellRuntimeException e) {
			if (!ctx.shouldSuppressErrors()) renderMessage(e.getMessage());
		}
	}
	
	static void renderMessage(String key) {
		// TODO
	}
	
}
