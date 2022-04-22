package gdavid.phi.spell.trick.hud;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import gdavid.phi.Phi;
import gdavid.phi.item.VisorItem;
import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.util.RenderHelper;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class HUDLineTrick extends PieceTrick {
	
	SpellParam<Vector3> a, b;
	SpellParam<Vector3> color;
	SpellParam<Number> size;
	
	public HUDLineTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(a = new ParamVector(SpellParam.GENERIC_NAME_VECTOR1, SpellParam.RED, false, false));
		addParam(b = new ParamVector(SpellParam.GENERIC_NAME_VECTOR2, SpellParam.GREEN, false, false));
		addParam(color = new ParamVector(Param.color.name, SpellParam.BLUE, true, false));
		addParam(size = new ParamNumber(Param.size.name, SpellParam.CYAN, true, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		meta.addStat(EnumSpellStat.POTENCY, 5);
	}
	
	@Override
	@SuppressWarnings("resource")
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (!(context.tool.getItem() instanceof VisorItem)) Errors.noVisor.runtime();
		Vector3 v1 = getNonnullParamValue(context, a);
		Vector3 v2 = getNonnullParamValue(context, b);
		Vector3 colorVal = getParamValue(context, color);
		float sizeVal = getParamValueOrDefault(context, size, 0.01f).floatValue();
		if (sizeVal <= 0) return null;
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			if (Minecraft.getInstance().gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) return;
			MainWindow win = (MainWindow) context.customData.get(Phi.modId + ":visor.window");
			float cx = win.getScaledWidth() / 2f;
			float cy = win.getScaledHeight() / 2f;
			float s = Math.min(cx, cy);
			Vector3 p1 = v1.copy().multiply(s, -s, 0).add(cx, cy, 0);
			Vector3 p2 = v2.copy().multiply(s, -s, 0).add(cx, cy, 0);
			Vector3 offset = p2.copy().sub(p1).normalize().multiply(sizeVal * s).rotate(Math.PI / 2, Vector3.forward);
			float r, g, b;
			if (colorVal == null) {
				ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
				ItemStack colorizer = cad == null ? ItemStack.EMPTY : ((ICAD) cad.getItem()).getComponentInSlot(cad, EnumCADComponent.DYE);
				int col = RenderHelper.getColorForColorizer(colorizer);
				r = RenderHelper.r(col) / 255.0f;
				g = RenderHelper.g(col) / 255.0f;
				b = RenderHelper.b(col) / 255.0f;
			} else {
				r = (float) Math.max(0, Math.min(1, colorVal.x));
				g = (float) Math.max(0, Math.min(1, colorVal.y));
				b = (float) Math.max(0, Math.min(1, colorVal.z));
			}
			GlStateManager.disableDepthTest();
			GlStateManager.depthMask(false);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buf = tess.getBuffer();
			buf.begin(7, DefaultVertexFormats.POSITION_COLOR);
			buf.pos(p1.x - offset.x, p1.y - offset.y, 0).color(r, g, b, 0.6f).endVertex();
			buf.pos(p1.x + offset.x, p1.y + offset.y, 0).color(r, g, b, 0.6f).endVertex();
			buf.pos(p2.x + offset.x, p2.y + offset.y, 0).color(r, g, b, 0.6f).endVertex();
			buf.pos(p2.x - offset.x, p2.y - offset.y, 0).color(r, g, b, 0.6f).endVertex();
			tess.draw();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			GlStateManager.enableDepthTest();
		});
		return null;
	}
	
}
