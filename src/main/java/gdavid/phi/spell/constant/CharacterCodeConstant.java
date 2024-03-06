package gdavid.phi.spell.constant;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

public class CharacterCodeConstant extends SpellPiece {
	
	public static final String tagValue = "value";
	
	public char ch;
	
	public CharacterCodeConstant(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		ch = 'A';
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("resource")
	public void drawAdditional(PoseStack ms, MultiBufferSource buffers, int light) {
		Font font = Minecraft.getInstance().font;
		ms.pushPose();
		String rstr = "§8" + String.valueOf(ch);
		ms.translate(8 - font.width(rstr) / 2f, 2, 0);
		font.drawInBatch(rstr, 0, 0, 0xffffff, false, ms.last().pose(), buffers, false, 0, light);
		ms.popPose();
		ms.pushPose();
		rstr = Integer.toString(ch);
		ms.translate(8 - font.width(rstr) / 4f, 10, 0);
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
		if (ch < 0x20 || ch > 0x7e) return false;
		if (doit) {
			this.ch = ch;
		}
		return true;
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		return (double) ch;
	}
	
	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);
		nbt.putString(tagValue, String.valueOf(ch));
	}
	
	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		String str = nbt.getString(tagValue);
		if (str.length() != 1) ch = '\0';
		else ch = str.charAt(0);
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Number.class;
	}
	
	@Override
	public EnumPieceType getPieceType() {
		return EnumPieceType.CONSTANT;
	}
	
	@Override
	public Object evaluate() throws SpellCompilationException {
		return (double) ch;
	}
	
}
