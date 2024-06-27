package gdavid.phi.spell.trick.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import gdavid.phi.gui.HUDContext;
import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.OnlyIn;

import gdavid.phi.Phi;
import gdavid.phi.item.VisorItem;
import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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

import java.util.List;

import static com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS;

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
	@OnlyIn(Dist.CLIENT)
	public void addToTooltipAfterShift(List<Component> tooltip) {
		tooltip.add(Component.translatable("phi.tooltip.require_visor"));
		super.addToTooltipAfterShift(tooltip);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		meta.addStat(EnumSpellStat.POTENCY, 5);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (!(context.tool.getItem() instanceof VisorItem)) Errors.noVisor.runtime();
		Vector3 v1 = getNonnullParamValue(context, a);
		Vector3 v2 = getNonnullParamValue(context, b);
		float[] col = RenderHelper.colorOrColorizer(this, context, color);
		float sizeVal = getParamValueOrDefault(context, size, 0.01f).floatValue();
		if (sizeVal <= 0) return null;
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			if (Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) return;
			var ctx = (HUDContext) context.customData.get(Phi.modId + ":visor.ctx");
			ctx.setup(context);
			Vector3 offset = v2.copy().sub(v1).normalize().multiply(sizeVal).crossProduct(Vector3.forward);
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			var mat = ctx.ms().last().pose();
			BufferBuilder buf = Tesselator.getInstance().getBuilder();
			buf.begin(QUADS, DefaultVertexFormat.POSITION_COLOR);
			buf.vertex(mat, (float) (v1.x - offset.x), (float) (v1.y - offset.y), (float) (v1.z - offset.z + 1)).color(col[0], col[1], col[2], 0.6f).endVertex();
			buf.vertex(mat, (float) (v1.x + offset.x), (float) (v1.y + offset.y), (float) (v1.z + offset.z + 1)).color(col[0], col[1], col[2], 0.6f).endVertex();
			buf.vertex(mat, (float) (v2.x + offset.x), (float) (v2.y + offset.y), (float) (v2.z + offset.z + 1)).color(col[0], col[1], col[2], 0.6f).endVertex();
			buf.vertex(mat, (float) (v2.x - offset.x), (float) (v2.y - offset.y), (float) (v2.z - offset.z + 1)).color(col[0], col[1], col[2], 0.6f).endVertex();
			BufferUploader.drawWithShader(buf.end());
			ctx.cleanup();
		});
		return null;
	}
	
}
