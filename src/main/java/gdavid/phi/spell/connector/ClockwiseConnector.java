package gdavid.phi.spell.connector;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.IGenericRedirector;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam.Any;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

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
	public void drawParams(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		// TODO fix this when there's an API that doesn't require a registered
		// SpellParam
		/*
		 * for (SpellParam.Side side : SpellParam.Side.values()) { if (!side.isEnabled()
		 * || !spell.grid.getPieceAtSideSafely(x, y,
		 * side).isInputSide(side.getOpposite())) { continue; } RenderHelper.param(ms,
		 * buffers, light, SpellParam.GRAY, ArrowType.IN, remapSide(side.getOpposite()),
		 * this); }
		 */
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
	
	@Override
	public boolean isInputSide(Side side) {
		// No recursive check to avoid dealing with infinite loops
		return spell.grid.getPieceAtSideSafely(x, y, reverseSide(side)) != null;
	}
	
}
