package gdavid.phi.spell.connector;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gdavid.phi.Phi;
import gdavid.phi.spell.Param;
import gdavid.phi.util.ParamHelper;
import gdavid.phi.util.RenderHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.ClientPsiAPI;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.IGenericRedirector;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Any;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;

public class BidirectionalConnector extends SpellPiece implements IGenericRedirector {
	
	public static final ResourceLocation lineTexture = new ResourceLocation(Phi.modId,
			"spell/connector_bidirectional_lines");
	public static final ResourceLocation hintTexture = new ResourceLocation(Phi.modId,
			"spell/connector_bidirectional_hint");
	
	ParamAny a, b;
	
	public BidirectionalConnector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(a = new ParamAny(Param.fromTo.name, SpellParam.GRAY, false));
		addParam(b = new ParamAny(Param.toFrom.name, SpellParam.PURPLE, false));
	}
	
	@Override
	public Side remapSide(Side side) {
		if (side.getOpposite() == paramSides.get(a)) return paramSides.get(b);
		if (side.getOpposite() == paramSides.get(b)) return paramSides.get(a);
		return Side.OFF;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawAdditional(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		if (!paramSides.get(a).isEnabled() && !paramSides.get(b).isEnabled()) {
			RenderMaterial material = new RenderMaterial(ClientPsiAPI.PSI_PIECE_TEXTURE_ATLAS, hintTexture);
			IVertexBuilder buffer = material.getBuffer(buffers, get -> SpellPiece.getLayer());
			Matrix4f mat = ms.getLast().getMatrix();
			buffer.pos(mat, 0, 16, 0).color(255, 255, 255, 255);
			buffer.tex(0, 1).lightmap(light).endVertex();
			buffer.pos(mat, 16, 16, 0).color(255, 255, 255, 255);
			buffer.tex(1, 1).lightmap(light).endVertex();
			buffer.pos(mat, 16, 0, 0).color(255, 255, 255, 255);
			buffer.tex(1, 0).lightmap(light).endVertex();
			buffer.pos(mat, 0, 0, 0).color(255, 255, 255, 255);
			buffer.tex(0, 0).lightmap(light).endVertex();
		}
		drawLine(ms, buffers, light, paramSides.get(a), false,
				ParamHelper.connectorColor(this, paramSides.get(a), SpellParam.GRAY));
		drawLine(ms, buffers, light, paramSides.get(a), true,
				ParamHelper.connectorColor(this, paramSides.get(b), SpellParam.PURPLE));
		drawLine(ms, buffers, light, paramSides.get(b), false,
				ParamHelper.connectorColor(this, paramSides.get(a), SpellParam.GRAY));
		drawLine(ms, buffers, light, paramSides.get(b), true,
				ParamHelper.connectorColor(this, paramSides.get(b), SpellParam.PURPLE));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void drawLine(MatrixStack ms, IRenderTypeBuffer buffers, int light, SpellParam.Side side, boolean which,
			int color) {
		if (!side.isEnabled()) {
			return;
		}
		RenderMaterial material = new RenderMaterial(ClientPsiAPI.PSI_PIECE_TEXTURE_ATLAS, lineTexture);
		IVertexBuilder buffer = material.getBuffer(buffers, get -> SpellPiece.getLayer());
		float minU = (side == SpellParam.Side.LEFT || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float minV = (side == SpellParam.Side.TOP || side == SpellParam.Side.BOTTOM) ? 0.25f : 0;
		if (which) minV += 0.5f;
		float maxU = minU + 0.5f, maxV = minV + 0.25f;
		int r = RenderHelper.r(color);
		int g = RenderHelper.g(color);
		int b = RenderHelper.b(color);
		Matrix4f mat = ms.getLast().getMatrix();
		buffer.pos(mat, 0, 16, 0).color(r, g, b, 255);
		buffer.tex(minU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, 16, 16, 0).color(r, g, b, 255);
		buffer.tex(maxU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, 16, 0, 0).color(r, g, b, 255);
		buffer.tex(maxU, minV).lightmap(light).endVertex();
		buffer.pos(mat, 0, 0, 0).color(r, g, b, 255);
		buffer.tex(minU, minV).lightmap(light).endVertex();
	}
	
	@Override
	public String getSortingName() {
		return "00000000000";
	}
	
	@Override
	public EnumPieceType getPieceType() {
		return EnumPieceType.CONNECTOR;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Any.class;
	}
	
	@Override
	public Object evaluate() throws SpellCompilationException {
		return null;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return null;
	}
	
}
