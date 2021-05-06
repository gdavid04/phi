package gdavid.phi.spell.other;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gdavid.phi.Phi;
import gdavid.phi.spell.ModPieces;
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
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;

public class BidirectionalConnector extends SpellPiece implements IGenericRedirector {
	
	public static final ResourceLocation lineTexture = new ResourceLocation(Phi.modId,
			"spell/connector_bidirectional_lines");
	
	ParamAny a, b;
	
	public BidirectionalConnector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(a = new ParamAny(ModPieces.Params.fromTo, SpellParam.GRAY, false));
		addParam(b = new ParamAny(ModPieces.Params.toFrom, SpellParam.GRAY, false));
	}
	
	@Override
	public Side remapSide(Side side) {
		if (side.getOpposite() == paramSides.get(a)) return paramSides.get(b);
		if (side.getOpposite() == paramSides.get(b)) return paramSides.get(a);
		return Side.OFF;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawParams(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		if (spell.grid.getPieceAtSideSafely(x, y, paramSides.get(a)) != null) {
			RenderHelper.doubleParam(ms, buffers, light, a.color, paramSides.get(a));
		} else {
			RenderHelper.param(ms, buffers, light, a.color, paramSides.get(a));
		}
		if (spell.grid.getPieceAtSideSafely(x, y, paramSides.get(b)) != null) {
			RenderHelper.doubleParam(ms, buffers, light, b.color, paramSides.get(b));
		} else {
			RenderHelper.param(ms, buffers, light, b.color, paramSides.get(b));
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawAdditional(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		drawLine(ms, buffers, light, paramSides.get(a));
		drawLine(ms, buffers, light, paramSides.get(b));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void drawLine(MatrixStack ms, IRenderTypeBuffer buffers, int light, SpellParam.Side side) {
		if (!side.isEnabled()) {
			return;
		}
		RenderMaterial material = new RenderMaterial(ClientPsiAPI.PSI_PIECE_TEXTURE_ATLAS, lineTexture);
		IVertexBuilder buffer = material.getBuffer(buffers, get -> SpellPiece.getLayer());
		float minU = (side == SpellParam.Side.LEFT || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float minV = (side == SpellParam.Side.TOP || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float maxU = minU + 0.5f, maxV = minV + 0.5f;
		int r = 255, g = 255, b = 255, a = 255;
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
	public String getSortingName() {
		return "00000000000";
	}
	
	@Override
	public EnumPieceType getPieceType() {
		return EnumPieceType.CONNECTOR;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return null;
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
