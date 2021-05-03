package gdavid.phi.spell.operator;

import com.mojang.blaze3d.matrix.MatrixStack;

import gdavid.phi.util.ISidedResult;
import gdavid.phi.util.ParamHelper;
import gdavid.phi.util.ReferenceParam;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class SplitVectorOperator extends PieceOperator {
	
	SpellParam<Vector3> vector;
	ReferenceParam outX, outY, outZ;
	
	public SplitVectorOperator(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(vector = new ParamVector(SpellParam.GENERIC_NAME_VECTOR, SpellParam.GREEN, false, false));
		addParam(outX = new ReferenceParam(SpellParam.GENERIC_NAME_X, SpellParam.RED, true));
		addParam(outY = new ReferenceParam(SpellParam.GENERIC_NAME_Y, SpellParam.GREEN, true));
		addParam(outZ = new ReferenceParam(SpellParam.GENERIC_NAME_Z, SpellParam.BLUE, true));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawParams(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		ParamHelper.draw(ms, buffers, light, vector.color, paramSides.get(vector));
	}
	
	// TODO drawAdditional
	
	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return new Result(getNonnullParamValue(context, vector), paramSides.get(outX), paramSides.get(outY), paramSides.get(outZ));
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
		public Object get(Side side) {
			if (side == x) return value.x;
			if (side == y) return value.y;
			if (side == z) return value.z;
			return null;
			/* TODO return rest of vector
			Vector3 res = value.copy();
			if (x != SpellParam.Side.OFF) res.x = 0;
			if (y != SpellParam.Side.OFF) res.y = 0;
			if (z != SpellParam.Side.OFF) res.z = 0;
			return res;
			*/
		}
		
	}
	
}
