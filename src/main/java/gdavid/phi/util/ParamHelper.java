package gdavid.phi.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

public class ParamHelper {
	
	public static double positiveOrZero(SpellPiece piece, SpellParam<Number> param) throws SpellCompilationException {
		double res = piece.getNonNullParamEvaluation(param).doubleValue();
		if (res < 0) {
			throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, piece.x, piece.y);
		}
		return res;
	}
	
	public static double positive(SpellPiece piece, SpellParam<Number> param) throws SpellCompilationException {
		double res = piece.getNonNullParamEvaluation(param).doubleValue();
		if (res <= 0) {
			throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, piece.x, piece.y);
		}
		return res;
	}
	
	public static Vector3 nonNull(SpellPiece piece, SpellContext context, SpellParam<Vector3> param)
			throws SpellRuntimeException {
		Vector3 res = piece.getNonnullParamValue(context, param);
		if (res.isZero()) {
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		}
		return res;
	}
	
	public static Vector3 inRange(SpellPiece piece, SpellContext context, SpellParam<Vector3> param)
			throws SpellRuntimeException {
		Vector3 res = nonNull(piece, context, param);
		if (!context.isInRadius(res)) {
			throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
		}
		return res;
	}
	
	public static BlockPos block(SpellPiece piece, SpellContext context, SpellParam<Vector3> param)
			throws SpellRuntimeException {
		return inRange(piece, context, param).toBlockPos();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void draw(MatrixStack ms, IRenderTypeBuffer buffers, int light, int color, SpellParam.Side side) {
		IVertexBuilder buffer = buffers.getBuffer(PsiAPI.internalHandler.getProgrammerLayer());
		if (!side.isEnabled()) {
			return;
		}
		int minX = 4 + side.offx * 9, minY = 4 + side.offy * 9;
		int maxX = minX + 8, maxY = minY + 8;
		float minU = side.u / 256f, minV = side.v / 256f;
		float maxU = minU + 1/32f, maxV = minV + 1/32f;
		int r = RenderHelper.r(color), g = RenderHelper.g(color), b = RenderHelper.b(color), a = 255;
		Matrix4f mat = ms.getLast().getMatrix();
		buffer.pos(mat, minX, maxY, 0).color(r, g, b, a).tex(minU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, maxX, maxY, 0).color(r, g, b, a).tex(maxU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, maxX, minY, 0).color(r, g, b, a).tex(maxU, minV).lightmap(light).endVertex();
		buffer.pos(mat, minX, minY, 0).color(r, g, b, a).tex(minU, minV).lightmap(light).endVertex();
	}
	
}
