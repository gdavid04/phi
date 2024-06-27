package gdavid.phi.spell.constant;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;

public class VectorConstant extends SpellPiece {
	
	public String[] components;
	
	public static int selectedComponent = 0;
	
	public VectorConstant(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		components = new String[] { "0", "0", "0" };
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("resource")
	public void drawAdditional(PoseStack ms, MultiBufferSource buffers, int light) {
		Font font = Minecraft.getInstance().font;
		ms.pushPose();
		ms.translate(2, 2, 0);
		ms.scale(0.5f, 0.5f, 1);
		for (int i = 0; i < 3; i++) {
			boolean selected = true;
			try {
				Class<?> clazz = Class.forName("vazkii.psi.client.gui.GuiProgrammer");
				selected = clazz.isInstance(Minecraft.getInstance().screen)
						&& clazz.getField("selectedX").getInt(null) == x
						&& clazz.getField("selectedY").getInt(null) == y;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (components[i].length() > 5) components[i] = "0";
			int color = (selected && selectedComponent == i) ? 0xffffff : 0x808080 | 0xff << ((2 - i) * 8);
			font.drawInBatch(components[i], 0, 0, color, false, ms.last().pose(), buffers, false, 0, light);
			ms.translate(0, 8, 0);
		}
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
		if (ch >= 'x' && ch <= 'z') selectedComponent = ch - 'x';
		else if (ch >= 'X' && ch <= 'Z') selectedComponent = ch - 'X';
		else {
			if ("FDfd".indexOf(ch) != -1) return false;
			String tmp = components[selectedComponent];
			if ((tmp.equals("0") || tmp.equals("-0")) && ch != '-' && ch != '+') tmp = tmp.replace("0", "");
			if (ch == '+') {
				if (tmp.startsWith("-")) tmp = tmp.substring(1);
			} else if (ch == '-') {
				if (!tmp.startsWith("-")) tmp = "-" + tmp;
			} else tmp = (tmp + ch).trim();
			if (tmp.length() > 5) {
				if (tmp.startsWith("0.")) tmp = tmp.substring(1);
				else if (tmp.startsWith("-0.")) tmp = "-" + tmp.substring(2);
			} else {
				if (tmp.startsWith(".")) tmp = "0" + tmp;
				else if (tmp.startsWith("-.")) tmp = "-0" + tmp.substring(1);
			}
			if (tmp.length() > 5) return false;
			if (tmp.isEmpty()) tmp = "0";
			try {
				Double.parseDouble(tmp);
			} catch (NumberFormatException e) {
				return false;
			}
			if (doit) components[selectedComponent] = tmp;
		}
		return true;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean onKeyPressed(int key, int scanCode, boolean doit) {
		if (components[selectedComponent].length() == 0) return false;
		if (key == GLFW.GLFW_KEY_BACKSPACE) {
			if (doit) {
				String tmp = components[selectedComponent];
				if (tmp.startsWith(".")) tmp = "0" + tmp;
				else if (tmp.startsWith("-.")) tmp = "-0" + tmp.substring(1);
				if (tmp.equals("-0")) tmp = "0";
				else if (tmp.startsWith("-") && tmp.length() == 2) tmp = "-0";
				else tmp = tmp.substring(0, tmp.length() - 1).trim();
				if (tmp.isEmpty()) tmp = "0";
				try {
					Double.parseDouble(tmp);
				} catch (NumberFormatException e) {
					return false;
				}
				if (doit) components[selectedComponent] = tmp;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public Object evaluate() {
		for (int i = 0; i < 3; i++)
			if (components[i].length() > 5) components[i] = "0";
		try {
			return new Vector3(Double.parseDouble(components[0]), Double.parseDouble(components[1]),
					Double.parseDouble(components[2]));
		} catch (NumberFormatException e) {
			return Vector3.zero;
		}
	}
	
	@Override
	public Object execute(SpellContext context) {
		return evaluate();
	}
	
	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);
		nbt.putString("x", components[0]);
		nbt.putString("y", components[1]);
		nbt.putString("z", components[2]);
	}
	
	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		components[0] = nbt.getString("x");
		components[1] = nbt.getString("y");
		components[2] = nbt.getString("z");
	}
	
	@Override
	public Class<?> getEvaluationType() {
		return Vector3.class;
	}
	
	@Override
	public EnumPieceType getPieceType() {
		return EnumPieceType.CONSTANT;
	}
	
}
