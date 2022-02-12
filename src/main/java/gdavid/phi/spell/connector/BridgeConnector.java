package gdavid.phi.spell.connector;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import gdavid.phi.Phi;
import gdavid.phi.util.IWarpRedirector;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.ClientPsiAPI;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Any;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamAny;

public class BridgeConnector extends SpellPiece implements IWarpRedirector {
	
	public static final ResourceLocation lineTexture = new ResourceLocation(Phi.modId,
			"spell/connector_bridge_lines");
	
	ParamAny direction;
	
	public BridgeConnector(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(direction = new ParamAny(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GRAY, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
	}
	
	@Override
	public boolean isInputSide(SpellParam.Side side) {
		return false;
	}
	
	@Override
	public SpellPiece redirect(Side side) {
		if (paramSides.get(direction) != Side.OFF) side = paramSides.get(direction);
		try {
			Class<?> clazz = Class.forName("vazkii.psi.common.spell.other.PieceConnector");
			SpellPiece connector = (SpellPiece) clazz.getConstructor(Spell.class).newInstance(spell);
			connector.paramSides.put((SpellParam<?>) clazz.getField("target").get(connector), side);
			connector.x = x + side.offx;
			connector.y = y + side.offy;
			return connector;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawParams(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		super.drawParams(ms, buffers, light);
		// TODO draw on top of other pieces
		if (paramSides.get(direction).isEnabled()) {
			drawLine(ms, buffers, light, paramSides.get(direction));
		} else {
			for (Side side : Side.values()) {
				if (!side.isEnabled()) continue;
				SpellPiece nb = spell.grid.getPieceAtSideSafely(x, y, side.getOpposite());
				if (nb != null && nb.isInputSide(side)) drawLine(ms, buffers, light, side);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	static RenderType lineLayer;
	
	@OnlyIn(Dist.CLIENT)
	public void drawLine(MatrixStack ms, IRenderTypeBuffer buffers, int light, Side side) {
		if (!side.isEnabled()) return;
		GlStateManager.enableBlend();
		GlStateManager.glBlendFuncSeparate(SourceFactor.SRC_ALPHA.param, DestFactor.ONE_MINUS_SRC_ALPHA.param, SourceFactor.ZERO.param, SourceFactor.ONE.param);
		RenderMaterial material = new RenderMaterial(ClientPsiAPI.PSI_PIECE_TEXTURE_ATLAS, lineTexture);
		if (lineLayer == null) {
			RenderType.State glState = RenderType.State.getBuilder()
					.texture(new RenderState.TextureState(ClientPsiAPI.PSI_PIECE_TEXTURE_ATLAS, false, false))
					.lightmap(new RenderState.LightmapState(true))
					.cull(new RenderState.CullState(false))
					.writeMask(new RenderState.WriteMaskState(true, false))
					.transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
						RenderSystem.enableBlend();
						RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					}, () -> {
						RenderSystem.disableBlend();
						RenderSystem.defaultBlendFunc();
					})).build(false);
			lineLayer = RenderType.makeType(lineTexture.toString(), DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 64, glState);
		}
		IVertexBuilder buffer = material.getBuffer(buffers, get -> lineLayer);
		float minU = (side == SpellParam.Side.LEFT || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float minV = (side == SpellParam.Side.TOP || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float maxU = minU + 0.5f, maxV = minV + 0.5f;
		int r = 255, g = 255, b = 255, a = 255;
		Matrix4f mat = ms.getLast().getMatrix();
		mat.translate(new Vector3f(side.offx * 18, side.offy * 18, 0));
		buffer.pos(mat, -8, 24, 0).color(r, g, b, a);
		buffer.tex(minU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, 24, 24, 0).color(r, g, b, a);
		buffer.tex(maxU, maxV).lightmap(light).endVertex();
		buffer.pos(mat, 24, -8, 0).color(r, g, b, a);
		buffer.tex(maxU, minV).lightmap(light).endVertex();
		buffer.pos(mat, -8, -8, 0).color(r, g, b, a);
		buffer.tex(minU, minV).lightmap(light).endVertex();
		GlStateManager.disableBlend();
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
