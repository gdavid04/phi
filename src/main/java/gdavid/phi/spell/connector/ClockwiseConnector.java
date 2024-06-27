package gdavid.phi.spell.connector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gdavid.phi.util.EvalHelper;
import gdavid.phi.util.SpellCachedView;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.SpellParam.Any;
import vazkii.psi.api.spell.SpellParam.ArrowType;
import vazkii.psi.api.spell.SpellParam.Side;

import java.util.*;

public class ClockwiseConnector extends SpellPiece implements IGenericRedirector {
	
	public ClockwiseConnector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
	}
	
	@Override
	public Side remapSide(Side side) {
		return side.rotateCCW();
	}
	
	/**
	 * Inverse of remapSide
	 */
	public Side reverseSide(Side side) {
		return side.rotateCW();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawParams(PoseStack ms, MultiBufferSource buffers, int light) {
		VertexConsumer buffer = buffers.getBuffer(PsiAPI.internalHandler.getProgrammerLayer());
		for (Side side : SpellParam.Side.values()) {
			if (!isInputSide(side)) continue;
			int index = 0, count = 1;
			SpellPiece neighbour = spell.grid.getPieceAtSideSafely(x, y, side);
			if (neighbour != null) {
				int nbcount = neighbour.getParamArrowCount(side.getOpposite());
				if (side.asInt() > side.getOpposite().asInt()) index += nbcount;
				count += nbcount;
			}
			float percent = count > 1 ? (float) index / (count - 1) : 0.5f;
			drawParam(ms, buffer, light, side, SpellParam.GRAY, ArrowType.IN, percent);
		}
	}
	
	@Override
	public int getParamArrowCount(Side side) {
		return isInputSide(side) ? 1 : 0;
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
	
	// Cache input sides to avoid recalculating them multiple times every frame
	private final SpellCachedView<Set<Side>> isInputSideView = new SpellCachedView<>(this, () -> {
		var res = new HashSet<Side>();
		for (Side side : Side.values()) {
			if (!side.isEnabled()) continue;
			SpellPiece piece = spell.grid.getPieceAtSideSafely(x, y, reverseSide(side).getOpposite());
			if (piece != null && piece.isInputSide(reverseSide(side))) res.add(side);
		}
		return res;
	}, Collections::emptySet);
	
	@Override
	public boolean isInputSide(Side side) {
		return isInputSideView.get().contains(side);
	}
	
}
