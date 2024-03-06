package gdavid.phi.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import gdavid.phi.Phi;
import gdavid.phi.network.Messages;
import gdavid.phi.network.ProgramTransferMessage;
import gdavid.phi.util.IProgramTransferTarget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.client.gui.GuiProgrammer;

@OnlyIn(Dist.CLIENT)
public class ProgramTransferWidget extends AbstractWidget {
	
	static final ResourceLocation texture = new ResourceLocation(Phi.modId, "textures/gui/program_transfer.png");
	
	final GuiProgrammer parent;
	final IProgramTransferTarget holder;
	final Direction dir;
	final boolean mirror;
	public final SlotSelectWidget select;
	
	public ProgramTransferWidget(GuiProgrammer parent, IProgramTransferTarget holder, boolean side, Direction dir) {
		super(parent.left + (side ? parent.xSize - 54 : 0), parent.top + parent.ySize + 12, 54, 16,
				Component.empty());
		this.parent = parent;
		this.holder = holder;
		this.dir = dir;
		this.mirror = side;
		select = new SlotSelectWidget(parent, this, holder, side, dir);
	}
	
	@Override
	public void renderButton(PoseStack ms, int mouseX, int mouseY, float partial) {
		if (parent.takingScreenshot) return;
		parent.getMinecraft().textureManager.bindForSetup(texture);
		drawButton(mouseX, mouseY, ms, 0, 0, 0);
		if (clickButton(mouseX, mouseY, 0, 0)) {
			parent.tooltip.add(Component.translatable(Phi.modId + ".program_transfer.write"));
		}
		drawButton(mouseX, mouseY, ms, 19, 0, 1);
		if (clickButton(mouseX, mouseY, 19, 0)) {
			parent.tooltip.add(Component.translatable(Phi.modId + ".program_transfer.read"));
		}
		if (holder.hasSlots()) {
			drawButton(mouseX, mouseY, ms, 38, 0, 2, select.active);
			if (clickButton(mouseX, mouseY, 38, 0)) {
				parent.tooltip.add(Component.translatable(Phi.modId + ".program_transfer.select_slot"));
			}
		}
	}
	
	@Override
	public void onClick(double x, double y) {
		if (clickButton(x, y, 0, 0)) {
			Messages.channel.sendToServer(new ProgramTransferMessage(parent.programmer.getBlockPos(), dir));
		} else if (clickButton(x, y, 19, 0)) {
			Messages.channel.sendToServer(new ProgramTransferMessage(holder.getPosition(), dir.getOpposite()));
		} else if (holder.hasSlots() && clickButton(x, y, 38, 0)) {
			select.show();
		}
	}
	
	void drawButton(int mx, int my, PoseStack ms, int x, int y, int id) {
		drawButton(mx, my, ms, x, y, id, false);
	}
	
	void drawButton(int mx, int my, PoseStack ms, int x, int y, int id, boolean pressed) {
		if (mirror) x = 54 - x - 16;
		blit(ms, this.x + x, this.y + y, 16 * id, pressed ? 16 : highlight(mx, my, x, y, 16, 16), 16, 16, 64, 32);
	}
	
	boolean clickButton(double mx, double my, int x, int y) {
		return checkMouse(mx, my, mirror ? 54 - x - 16 : x, y, 16, 16);
	}
	
	int highlight(int mx, int my, int x, int y, int w, int h) {
		return checkMouse(mx, my, x, y, w, h) ? 16 : 0;
	}
	
	boolean checkMouse(double mx, double my, int x, int y, int w, int h) {
		return !select.active && mx >= this.x + x && mx < this.x + x + w && my >= this.y + y && my < this.y + y + h;
	}
	
	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {}
	
}
