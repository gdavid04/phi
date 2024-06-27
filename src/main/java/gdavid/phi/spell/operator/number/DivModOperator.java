package gdavid.phi.spell.operator.number;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gdavid.phi.Phi;
import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.ReferenceParam;
import gdavid.phi.util.ISidedResult;
import gdavid.phi.util.ParamHelper;
import gdavid.phi.util.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.ClientPsiAPI;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.ArrowType;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DivModOperator extends PieceOperator {
	
	public static final ResourceLocation lineTexture = new ResourceLocation(Phi.modId, "spell/operator_div_mod_lines");
	
	public SpellParam<Number> a, b;
	ReferenceParam div, mod;
	
	public DivModOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(a = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER1, SpellParam.RED, false, false));
		addParam(b = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER2, SpellParam.GREEN, false, false));
		addParam(div = new ReferenceParam(Param.div.name, SpellParam.RED, true, true, ArrowType.NONE));
		addParam(mod = new ReferenceParam(Param.mod.name, SpellParam.GREEN, true, true, ArrowType.NONE));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addToTooltipAfterShift(List<Component> tooltip) {
		ParamHelper.outputTooltip(this, super::addToTooltipAfterShift, tooltip);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawAdditional(PoseStack ms, MultiBufferSource buffers, int light) {
		drawLine(ms, buffers, light, 0xffffffff, paramSides.get(a));
		drawLine(ms, buffers, light, 0xffffffff, paramSides.get(b));
		drawLine(ms, buffers, light, div.color, paramSides.get(div));
		drawLine(ms, buffers, light, mod.color, paramSides.get(mod));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void drawLine(PoseStack ms, MultiBufferSource buffers, int light, int color, SpellParam.Side side) {
		if (!side.isEnabled()) {
			return;
		}
		Material material = new Material(ClientPsiAPI.PSI_PIECE_TEXTURE_ATLAS, lineTexture);
		VertexConsumer buffer = material.buffer(buffers, get -> SpellPiece.getLayer());
		float minU = (side == SpellParam.Side.LEFT || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float minV = (side == SpellParam.Side.TOP || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float maxU = minU + 0.5f, maxV = minV + 0.5f;
		int r = RenderHelper.r(color), g = RenderHelper.g(color), b = RenderHelper.b(color), a = 255;
		Matrix4f mat = ms.last().pose();
		buffer.vertex(mat, 0, 16, 0).color(r, g, b, a);
		buffer.uv(minU, maxV).uv2(light).endVertex();
		buffer.vertex(mat, 16, 16, 0).color(r, g, b, a);
		buffer.uv(maxU, maxV).uv2(light).endVertex();
		buffer.vertex(mat, 16, 0, 0).color(r, g, b, a);
		buffer.uv(maxU, minV).uv2(light).endVertex();
		buffer.vertex(mat, 0, 0, 0).color(r, g, b, a);
		buffer.uv(minU, minV).uv2(light).endVertex();
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
	@Override
	public boolean isInputSide(Side side) {
		return paramSides.get(a) == side || paramSides.get(b) == side;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Double av = getParamValue(context, a).doubleValue();
		Double bv = getParamValue(context, b).doubleValue();
		if (bv == 0) Errors.runtime(SpellRuntimeException.DIVIDE_BY_ZERO);
		return new Result(av, bv, paramSides.get(div), paramSides.get(mod));
	}
	
	public static class Result implements ISidedResult {
		
		public final Double div, mod;
		public final SpellParam.Side sdiv, smod;
		
		public Result(double a, double b, Side sdiv, Side smod) {
			div = Math.floor(a / b);
			mod = a % b;
			this.sdiv = sdiv;
			this.smod = smod;
		}
		
		@Override
		public Object get(Side side) throws SpellRuntimeException {
			if (side == sdiv) return div;
			if (side == smod) return mod;
			Errors.runtime(SpellCompilationException.INVALID_PARAM);
			return null;
		}
		
	}
	
}
