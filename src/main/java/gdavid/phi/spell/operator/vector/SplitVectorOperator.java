package gdavid.phi.spell.operator.vector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gdavid.phi.Phi;
import gdavid.phi.spell.Errors;
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
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.ArrowType;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;

import java.util.List;

public class SplitVectorOperator extends SpellPiece {
	
	public static final ResourceLocation lineTexture = new ResourceLocation(Phi.modId,
			"spell/operator_split_vector_lines");
	
	public SpellParam<Vector3> vector;
	ReferenceParam outX, outY, outZ;
	
	public SplitVectorOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(vector = new ParamVector(SpellParam.GENERIC_NAME_VECTOR, SpellParam.GRAY, false, false));
		addParam(outX = new ReferenceParam(SpellParam.GENERIC_NAME_X, SpellParam.RED, true, true, ArrowType.NONE));
		addParam(outY = new ReferenceParam(SpellParam.GENERIC_NAME_Y, SpellParam.GREEN, true, true, ArrowType.NONE));
		addParam(outZ = new ReferenceParam(SpellParam.GENERIC_NAME_Z, SpellParam.BLUE, true, true, ArrowType.NONE));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addToTooltipAfterShift(List<Component> tooltip) {
		ParamHelper.outputTooltip(this, super::addToTooltipAfterShift, tooltip);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		int components = 0;
		if (paramSides.get(outX).isEnabled()) components++;
		if (paramSides.get(outY).isEnabled()) components++;
		if (paramSides.get(outZ).isEnabled()) components++;
		meta.addStat(EnumSpellStat.COMPLEXITY, components);
		if (components > 1) meta.addStat(EnumSpellStat.COMPLEXITY, 1);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawAdditional(PoseStack ms, MultiBufferSource buffers, int light) {
		drawLine(ms, buffers, light, 0xffffffff, paramSides.get(vector));
		drawLine(ms, buffers, light, outX.color, paramSides.get(outX));
		drawLine(ms, buffers, light, outY.color, paramSides.get(outY));
		drawLine(ms, buffers, light, outZ.color, paramSides.get(outZ));
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
	public EnumPieceType getPieceType() {
		return EnumPieceType.OPERATOR;
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
	@Override
	public Object evaluate() throws SpellCompilationException {
		return null;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return new Result(getNonnullParamValue(context, vector), paramSides.get(outX), paramSides.get(outY),
				paramSides.get(outZ));
	}
	
	public static class Result implements ISidedResult {
		
		public final Vector3 value;
		public final SpellParam.Side x, y, z;
		
		public Result(Vector3 value, SpellParam.Side x, SpellParam.Side y, SpellParam.Side z) {
			this.value = value;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public Object get(Side side) throws SpellRuntimeException {
			if (side == x) return value.x;
			if (side == y) return value.y;
			if (side == z) return value.z;
			Errors.runtime(SpellCompilationException.INVALID_PARAM);
			return null;
		}
		
	}
	
	@Override
	public boolean isInputSide(Side side) {
		return paramSides.get(vector) == side;
	}
	
}
