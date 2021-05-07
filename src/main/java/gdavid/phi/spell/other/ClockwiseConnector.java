package gdavid.phi.spell.other;

import com.mojang.blaze3d.matrix.MatrixStack;
import gdavid.phi.util.ParamHelper;
import gdavid.phi.util.RenderHelper;
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
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

public class ClockwiseConnector extends SpellPiece implements IGenericRedirector {
	
	public ClockwiseConnector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 2);
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
		for (SpellParam.Side side : SpellParam.Side.values()) {
			if (!side.isEnabled()
					|| !ParamHelper.checkSide(spell.grid.getPieceAtSideSafely(x, y, side), side.getOpposite())) {
				continue;
			}
			RenderHelper.param(ms, buffers, light, SpellParam.GRAY, remapSide(side.getOpposite()), this);
		}
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
