package gdavid.phi.spell.connector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import gdavid.phi.Phi;
import gdavid.phi.util.IWarpRedirector;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
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

import static com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS;

public class BridgeConnector extends SpellPiece implements IWarpRedirector {
	
	public static final ResourceLocation lineTexture = new ResourceLocation(Phi.modId, "spell/connector_bridge_lines");
	
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
	public void drawParams(PoseStack ms, MultiBufferSource buffers, int light) {
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
	public void drawLine(PoseStack ms, MultiBufferSource buffers, int light, Side side) {
		if (!side.isEnabled()) return;
		GlStateManager._enableBlend();
		GlStateManager.glBlendFuncSeparate(SourceFactor.SRC_ALPHA.value, DestFactor.ONE_MINUS_SRC_ALPHA.value,
				SourceFactor.ZERO.value, SourceFactor.ONE.value);
		Material material = new Material(ClientPsiAPI.PSI_PIECE_TEXTURE_ATLAS, lineTexture);
		if (lineLayer == null) {
			RenderType.CompositeState glState = RenderType.CompositeState.builder()
					.setTextureState(new RenderStateShard.TextureStateShard(ClientPsiAPI.PSI_PIECE_TEXTURE_ATLAS, false, false))
					.setLightmapState(new RenderStateShard.LightmapStateShard(true)).setCullState(new RenderStateShard.CullStateShard(false))
					.setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
					.setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
						RenderSystem.enableBlend();
						RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
								GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
								GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					}, () -> {
						RenderSystem.disableBlend();
						RenderSystem.defaultBlendFunc();
					})).createCompositeState(false);
			lineLayer = RenderType.create(lineTexture.toString(), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
					QUADS, 64, false, false, glState);
		}
		VertexConsumer buffer = material.buffer(buffers, get -> lineLayer);
		float minU = (side == SpellParam.Side.LEFT || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float minV = (side == SpellParam.Side.TOP || side == SpellParam.Side.BOTTOM) ? 0.5f : 0;
		float maxU = minU + 0.5f, maxV = minV + 0.5f;
		int r = 255, g = 255, b = 255, a = 255;
		Matrix4f mat = ms.last().pose();
		mat.translate(new Vector3f(side.offx * 18, side.offy * 18, 0));
		buffer.vertex(mat, -8, 24, 0).color(r, g, b, a);
		buffer.uv(minU, maxV).uv2(light).endVertex();
		buffer.vertex(mat, 24, 24, 0).color(r, g, b, a);
		buffer.uv(maxU, maxV).uv2(light).endVertex();
		buffer.vertex(mat, 24, -8, 0).color(r, g, b, a);
		buffer.uv(maxU, minV).uv2(light).endVertex();
		buffer.vertex(mat, -8, -8, 0).color(r, g, b, a);
		buffer.uv(minU, minV).uv2(light).endVertex();
		GlStateManager._disableBlend();
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
