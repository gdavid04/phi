package gdavid.phi.spell.constant;

import com.mojang.blaze3d.vertex.PoseStack;
import gdavid.phi.spell.Param;
import gdavid.phi.spell.param.TextParam;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.spell.*;

public class FormattingCodeConstant extends SpellPiece {
	
	public static final String tagValue = "value";
	
	SpellParam<String> prefix;
	
	public char code;
	
	public FormattingCodeConstant(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(prefix = new TextParam(Param.pre.name, SpellParam.GRAY, true, true));
		code = '-';
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawAdditional(PoseStack ms, MultiBufferSource buffers, int light) {
		Font font = Minecraft.getInstance().font;
		ms.pushPose();
		var format = ChatFormatting.getByCode(code);
		if (format == null) format = ChatFormatting.RESET;
		var rstr = FormattedCharSequence.codepoint(167, Style.EMPTY.applyLegacyFormat(format));
		if (code != '-') rstr = FormattedCharSequence.fromPair(rstr, FormattedCharSequence.codepoint(code, Style.EMPTY));
		ms.translate(8 - font.width(rstr) / 2f, 4, 0);
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
		if (ch != '-' && ChatFormatting.getByCode(ch) == null) return false;
		if (doit) {
			this.code = ch;
		}
		return true;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return getParamValueOrDefault(context, prefix, "") + "\u00a7" + (code == '-' ? "" : code);
	}
	
	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);
		nbt.putString(tagValue, String.valueOf(code));
	}
	
	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		String str = nbt.getString(tagValue);
		if (str.length() != 1) code = '-';
		else {
			code = str.charAt(0);
			if (ChatFormatting.getByCode(code) == null) code = '-';
		}
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
		return getParamEvaluationeOrDefault(prefix, "") + "\u00a7" + (code == '-' ? "" : code);
	}
	
}
