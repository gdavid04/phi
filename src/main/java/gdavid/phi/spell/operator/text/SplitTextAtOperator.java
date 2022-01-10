package gdavid.phi.spell.operator.text;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gdavid.phi.Phi;
import gdavid.phi.spell.Errors;
import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.ReferenceParam;
import gdavid.phi.spell.param.TextParam;
import gdavid.phi.util.ISidedResult;
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
import vazkii.psi.api.spell.SpellParam.ArrowType;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceOperator;

public class SplitTextAtOperator extends PieceOperator {
	
	public static final ResourceLocation lineTexture = new ResourceLocation(Phi.modId,
			"spell/operator_split_text_at_lines");
	
	public SpellParam<String> text, at;
	ReferenceParam before, after;
	
	public SplitTextAtOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(text = new TextParam(Param.text.name, SpellParam.GRAY, false, false));
		addParam(at = new TextParam(Param.at.name, SpellParam.BLUE, false, false));
		addParam(before = new ReferenceParam(Param.before.name, SpellParam.RED, true, ArrowType.NONE));
		addParam(after = new ReferenceParam(Param.after.name, SpellParam.GREEN, true, ArrowType.NONE));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawAdditional(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		drawLine(ms, buffers, light, 0xffffffff, paramSides.get(text));
		drawLine(ms, buffers, light, 0xffffffff, paramSides.get(at));
		drawLine(ms, buffers, light, before.color, paramSides.get(before));
		drawLine(ms, buffers, light, after.color, paramSides.get(after));
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
		return String.class;
	}
	
	@Override
	public boolean isInputSide(Side side) {
		return paramSides.get(text) == side || paramSides.get(at) == side;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		String str = getParamValue(context, text);
		String substr = getParamValue(context, at);
		int pos = str.indexOf(substr);
		if (pos == -1) Errors.runtime(SpellRuntimeException.NULL_TARGET);
		return new Result(str.substring(0, pos), str.substring(pos + substr.length()), paramSides.get(before),
				paramSides.get(after));
	}
	
	public static class Result implements ISidedResult {
		
		public final String before, after;
		public final SpellParam.Side sbefore, safter;
		
		public Result(String before, String after, Side sbefore, Side safter) {
			this.before = before;
			this.after = after;
			this.sbefore = sbefore;
			this.safter = safter;
		}
		
		@Override
		public Object get(Side side) throws SpellRuntimeException {
			if (side == sbefore) return before;
			if (side == safter) return after;
			Errors.runtime(SpellCompilationException.INVALID_PARAM);
			return null;
		}
		
	}
	
}
