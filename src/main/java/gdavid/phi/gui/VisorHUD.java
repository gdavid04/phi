package gdavid.phi.gui;

import java.util.Stack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import gdavid.phi.Phi;
import gdavid.phi.item.VisorItem;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.CompiledSpell.Action;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;

import static com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS;

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
			if (cad == null || cad.isEmpty()) {
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
				if (!ctx.shouldSuppressErrors()) errorMessage(hudContext, I18n.get(e.getMessage()), false);
			}
		} finally {
			Minecraft.getInstance().getProfiler().pop();
		}
	}
	
	static void errorMessage(HUDContext ctx, String message, boolean startup) {
		if (Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) return;
		
		ctx.ms().pushPose();
		ctx.ms().mulPose(Vector3f.YN.rotationDegrees(180 + Minecraft.getInstance().player.getViewYRot(ctx.partialTicks())));
		ctx.ms().translate(0, 0, -1);
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		if (startup) {
			float shift = 0.2f * ((ctx.partialTicks() + Minecraft.getInstance().level.getGameTime()) * 0.05f % 1);
			int stripes = 15;
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			var mat = ctx.ms().last().pose();
			BufferBuilder buf = Tesselator.getInstance().getBuilder();
			buf.begin(QUADS, DefaultVertexFormat.POSITION_COLOR);
			for (int i = 0; i < stripes; i++) {
				buf.vertex(mat, -1.3f + shift + i * 0.2f, 0.3f, 0).color(1, 0, 0, 0.4f).endVertex();
				buf.vertex(mat, -1.5f + shift + i * 0.2f, -0.2f, 0).color(1, 0, 0, 0.4f).endVertex();
				buf.vertex(mat, -1.5f + shift + i * 0.2f + 0.1f, -0.2f, 0).color(1, 0, 0, 0.4f).endVertex();
				buf.vertex(mat, -1.3f + shift + i * 0.2f + 0.1f, 0.3f, 0).color(1, 0, 0, 0.4f).endVertex();
			}
			buf.vertex(mat, -1.3f, 0.3f, 0).color(1, 0, 0, 1f).endVertex();
			buf.vertex(mat, -1.3f, 0.29f, 0).color(1, 0, 0, 1f).endVertex();
			buf.vertex(mat, 0.6f, 0.29f, 0).color(1, 0, 0, 1f).endVertex();
			buf.vertex(mat, 0.6f, 0.3f, 0).color(1, 0, 0, 1f).endVertex();
			BufferUploader.drawWithShader(buf.end());
			
			ctx.ms().pushPose();
			ctx.ms().translate(-0.8f, 0.2f, 0);
			ctx.ms().scale(0.005f, -0.005f, 0.005f);
			Minecraft.getInstance().font.draw(ctx.ms(), message, 0, 0, 0xff0000);
			ctx.ms().popPose();
		} else {
			ctx.ms().pushPose();
			ctx.ms().translate(-0.6f, -0.5f, 0);
			ctx.ms().scale(0.003f, -0.003f, 0.003f);
			ctx.ms().mulPose(Vector3f.XP.rotationDegrees(30));
			Minecraft.getInstance().font.draw(ctx.ms(), message, 0, 0, 0xff0000);
			ctx.ms().popPose();
		}
		
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.enableCull();
		ctx.ms().popPose();
	}
	
}
