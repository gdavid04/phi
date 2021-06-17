package gdavid.phi.spell.other;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import gdavid.phi.spell.ModPieces;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.ClientPsiAPI;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.IGenericRedirector;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.ArrowType;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;

public class InOutConnector extends SpellPiece implements IGenericRedirector {
	
	public ParamAny from, bidir, to;
	
	public InOutConnector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
	}
	
	@Override
	public void initParams() {
		addParam(from = new ParamAny(ModPieces.Params.from, SpellParam.GRAY, false));
		addParam(bidir = new ParamAny(ModPieces.Params.fromTo, SpellParam.GRAY, false));
		addParam(to = new ParamAny(ModPieces.Params.to, SpellParam.GRAY, false, ArrowType.NONE));
	}
	
	@Override
	public Side remapSide(Side side) {
		if (side.getOpposite() == paramSides.get(bidir)) return paramSides.get(from);
		if (side.getOpposite() == paramSides.get(to)) return paramSides.get(bidir);
		return Side.OFF;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawAdditional(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		drawLine(ms, buffers, light, paramSides.get(from), true, false);
		drawLine(ms, buffers, light, paramSides.get(bidir), true, true);
		drawLine(ms, buffers, light, paramSides.get(to), false, true);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void drawLine(MatrixStack ms, IRenderTypeBuffer buffers, int light, SpellParam.Side side, boolean in,
			boolean out) {
		if (!side.isEnabled()) {
			return;
		}
		RenderMaterial material = new RenderMaterial(ClientPsiAPI.PSI_PIECE_TEXTURE_ATLAS,
				BidirectionalConnector.lineTexture);
		IVertexBuilder buffer = material.getBuffer(buffers, get -> SpellPiece.getLayer());
		int minX = 0, minY = 0, maxX = 16, maxY = 16;
		float minU = (side == SpellParam.Side.LEFT || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float minV = (side == SpellParam.Side.TOP || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float maxU = minU + 0.5f, maxV = minV + 0.5f;
		if (side == SpellParam.Side.LEFT || side == SpellParam.Side.RIGHT) {
			if (!in) {
				minY += 8;
				minV += 0.25f;
			}
			if (!out) {
				maxY -= 8;
				maxV -= 0.25f;
			}
		} else if (side == SpellParam.Side.TOP || side == SpellParam.Side.BOTTOM) {
			if (!in) {
				minX += 8;
				minU += 0.25f;
			}
			if (!out) {
				maxX -= 8;
				maxU -= 0.25f;
			}
		}
		int r = 255, g = 255, b = 255, a = 255;
		Matrix4f mat = ms.getLast().getMatrix();
		buffer.pos(mat, minX, maxY, 0).color(r, g, b, a);
		buffer.tex(minU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, maxX, maxY, 0).color(r, g, b, a);
		buffer.tex(maxU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, maxX, minY, 0).color(r, g, b, a);
		buffer.tex(maxU, minV).lightmap(light).endVertex();
		buffer.pos(mat, minX, minY, 0).color(r, g, b, a);
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
	
	@Override
	public boolean isInputSide(Side side) {
		return paramSides.get(from) == side || paramSides.get(bidir) == side;
	}
	
}
