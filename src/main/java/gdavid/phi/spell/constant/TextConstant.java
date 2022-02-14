package gdavid.phi.spell.constant;

import com.mojang.blaze3d.matrix.MatrixStack;

import gdavid.phi.api.param.TextParam;
import gdavid.phi.spell.Param;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.nbt.CompoundNBT;
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
	
	// TODO consider allowing 10 or 15 character long constants
	
	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("resource")
	public void drawAdditional(MatrixStack ms, IRenderTypeBuffer buffers, int light) {
		if (str.length() > 5) str = str.substring(0, 5);
		FontRenderer font = Minecraft.getInstance().fontRenderer;
		String rstr = str.replaceAll(" ", "§8_§r");
		if (rstr.length() == 0) rstr = "§8Text";
		ms.push();
		ms.translate(8 - font.getStringWidth(rstr) / 4f, 4, 0);
		ms.scale(0.5f, 0.5f, 1);
		font.renderString(rstr, 0, 0, 0xffffff, false, ms.getLast().getMatrix(), buffers, false, 0, light);
		ms.pop();
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
	public void writeToNBT(CompoundNBT nbt) {
		super.writeToNBT(nbt);
		nbt.putString(tagValue, str);
	}
	
	@Override
	public void readFromNBT(CompoundNBT nbt) {
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
