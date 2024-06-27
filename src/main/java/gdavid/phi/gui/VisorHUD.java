package gdavid.phi.gui;

import java.util.Stack;

import com.mojang.blaze3d.vertex.PoseStack;
import gdavid.phi.Phi;
import gdavid.phi.item.VisorItem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.CompiledSpell.Action;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;

@OnlyIn(Dist.CLIENT)
public class VisorHUD {
	
	public static void renderWorld(PoseStack ms, RenderBuffers buffers, Camera camera, float partialTicks) {
		Minecraft.getInstance().getProfiler().push(Phi.modId + ":visor-hud");
		try {
			var hudContext = new HUDContext(ms, buffers, camera, partialTicks);
			Player player = Minecraft.getInstance().player;
			ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
			if (!(helmet.getItem() instanceof VisorItem)) return;
			ItemStack cad = PsiAPI.getPlayerCAD(player);
			if (cad == null) {
				errorMessage(hudContext, I18n.get(helmet.getItem().getDescriptionId() + ".no_cad"), true);
				return;
			}
			Spell spell = ISpellAcceptor.acceptor(helmet).getSpell();
			if (spell == null) return;
			SpellContext ctx = new SpellContext().setPlayer(player).setSpell(spell);
			ctx.tool = helmet;
			if (!ctx.isValid()) {
				errorMessage(hudContext, I18n.get(helmet.getItem().getDescriptionId() + ".invalid_spell"), true);
				return;
			}
			if (!ctx.cspell.metadata.evaluateAgainst(cad)) {
				errorMessage(hudContext, I18n.get(helmet.getItem().getDescriptionId() + ".weak_cad"), true);
				return;
			}
			ctx.customData.put(Phi.modId + ":visor.ctx", hudContext);
			// TODO piece safety check
			ctx.actions = (Stack<Action>) ctx.cspell.actions.clone();
			try {
				ctx.cspell.execute(ctx); // Bypass the non-client side check by calling execute directly
			} catch (SpellRuntimeException e) {
				if (!ctx.shouldSuppressErrors()) errorMessage(hudContext, e.getMessage(), false);
			}
		} finally {
			Minecraft.getInstance().getProfiler().pop();
		}
	}
	
	static void errorMessage(HUDContext ctx, String message, boolean startup) {
		// TODO
	}
	
}
