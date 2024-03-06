package gdavid.phi.spell.constant;

import com.mojang.blaze3d.vertex.PoseStack;
import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.TextParam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

public class TextConstant extends SpellPiece {
	
	public static final String tagValue = "value";
	
	SpellParam<String> prefix;
	
	public String str;
	
	public TextConstant(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(prefix = new TextParam(Param.pre.name, SpellParam.GRAY, true, true));
		str = "";
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("resource")
	public void drawAdditional(PoseStack ms, MultiBufferSource buffers, int light) {
		if (str.length() > 5) str = str.substring(0, 5);
		Font font = Minecraft.getInstance().font;
		String rstr = str.replaceAll(" ", "§8_§r");
		if (rstr.length() == 0) rstr = "§8Text";
		ms.pushPose();
		ms.translate(8 - font.width(rstr) / 4f, 4, 0);
		ms.scale(0.5f, 0.5f, 1);
		font.drawInBatch(rstr, 0, 0, 0xffffff, false, ms.last().pose(), buffers, false, 0, light);
		ms.popPose();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean interceptKeystrokes() {
		return true;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean onCharTyped(char ch, int key, boolean doit) {
		if (str.length() + 1 > 5) return false;
		if (ch < 0x20 || ch > 0x7e) return false;
		if (doit) {
			str = str + ch;
		}
		return true;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean onKeyPressed(int key, int scanCode, boolean doit) {
		if (str.length() == 0) return false;
		if (key == GLFW.GLFW_KEY_BACKSPACE) {
			if (doit) {
				str = str.substring(0, str.length() - 1);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		if (str.length() > 5) str = str.substring(0, 5);
		if (paramSides.get(prefix).isEnabled()) {
			return getParamValue(context, prefix) + str;
		}
		return str;
	}
	
	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);
		nbt.putString(tagValue, str);
	}
	
	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		str = nbt.getString(tagValue);
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return String.class;
	}
	
	@Override
	public EnumPieceType getPieceType() {
		return EnumPieceType.CONSTANT;
	}
	
	@Override
	public Object evaluate() throws SpellCompilationException {
		if (str.length() > 5) str = str.substring(0, 5);
		if (paramSides.get(prefix).isEnabled()) {
			return getParamEvaluation(prefix) + str;
		}
		return str;
	}
	
}
