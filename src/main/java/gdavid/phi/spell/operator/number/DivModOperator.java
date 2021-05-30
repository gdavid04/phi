package gdavid.phi.spell.operator.number;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gdavid.phi.Phi;
import gdavid.phi.spell.ModPieces;
import gdavid.phi.util.ISidedResult;
import gdavid.phi.util.ReferenceParam;
import gdavid.phi.util.RenderHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.ClientPsiAPI;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

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
		addParam(div = new ReferenceParam(ModPieces.Params.div, SpellParam.RED, true));
		addParam(mod = new ReferenceParam(ModPieces.Params.mod, SpellParam.GREEN, true));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawParams(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		RenderHelper.param(ms, buffers, light, a.color, paramSides.get(a));
		RenderHelper.param(ms, buffers, light, b.color, paramSides.get(b));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawAdditional(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		drawLine(ms, buffers, light, 0xffffffff, paramSides.get(a));
		drawLine(ms, buffers, light, 0xffffffff, paramSides.get(b));
		drawLine(ms, buffers, light, div.color, paramSides.get(div));
		drawLine(ms, buffers, light, mod.color, paramSides.get(mod));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void drawLine(MatrixStack ms, IRenderTypeBuffer buffers, int light, int color, SpellParam.Side side) {
		if (!side.isEnabled()) {
			return;
		}
		RenderMaterial material = new RenderMaterial(ClientPsiAPI.PSI_PIECE_TEXTURE_ATLAS, lineTexture);
		IVertexBuilder buffer = material.getBuffer(buffers, get -> SpellPiece.getLayer());
		float minU = (side == SpellParam.Side.LEFT || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float minV = (side == SpellParam.Side.TOP || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float maxU = minU + 0.5f, maxV = minV + 0.5f;
		int r = RenderHelper.r(color), g = RenderHelper.g(color), b = RenderHelper.b(color), a = 255;
		Matrix4f mat = ms.getLast().getMatrix();
		buffer.pos(mat, 0, 16, 0).color(r, g, b, a);
		buffer.tex(minU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, 16, 16, 0).color(r, g, b, a);
		buffer.tex(maxU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, 16, 0, 0).color(r, g, b, a);
		buffer.tex(maxU, minV).lightmap(light).endVertex();
		buffer.pos(mat, 0, 0, 0).color(r, g, b, a);
		buffer.tex(minU, minV).lightmap(light).endVertex();
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Double av = getParamValue(context, a).doubleValue();
		Double bv = getParamValue(context, b).doubleValue();
		if (bv == 0) throw new SpellRuntimeException(SpellRuntimeException.DIVIDE_BY_ZERO);
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
			throw new SpellRuntimeException(SpellCompilationException.INVALID_PARAM);
		}
		
	}
	
}
