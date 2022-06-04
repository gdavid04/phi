package gdavid.phi.gui.widget;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.matrix.MatrixStack;
import gdavid.phi.Phi;
import gdavid.phi.network.Messages;
import gdavid.phi.network.ProgramTransferSlotMessage;
import gdavid.phi.util.IProgramTransferTarget;
import java.util.List;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.psi.client.gui.GuiProgrammer;

@OnlyIn(Dist.CLIENT)
public class SlotSelectWidget extends Widget {
	
	static final ResourceLocation texture = new ResourceLocation(Phi.modId, "textures/gui/program_transfer.png");
	
	static final int[] px = new int[] { 30, 27, 11, -8, -27, -43, -46, -43, -27, -8, 11, 27 };
	static final int[] py = new int[] { -8, 11, 27, 30, 27, 11, -8, -27, -43, -46, -43, -27 };
	
	final GuiProgrammer parent;
	final ProgramTransferWidget transfer;
	final IProgramTransferTarget holder;
	final Direction dir;
	
	public SlotSelectWidget(GuiProgrammer parent, ProgramTransferWidget transfer, IProgramTransferTarget holder,
			boolean side, Direction dir) {
		super(parent.left + (side ? parent.xSize - 92 : 0), parent.top + parent.ySize - 26, 92, 92,
				StringTextComponent.EMPTY);
		this.parent = parent;
		this.transfer = transfer;
		this.holder = holder;
		this.dir = dir;
		hide();
	}
	
	@Override
	@SuppressWarnings("resource")
	public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partial) {
		if (parent.takingScreenshot) return;
		List<Integer> slots = holder.getSlots();
		List<ResourceLocation> icons = holder.getSlotIcons();
		if (slots.stream().max(Integer::compare).get() >= 12 || slots.stream().min(Integer::compare).get() < 0) {
			int i = 0;
			for (ResourceLocation icon : icons) {
				int x = 19 * (i % 5), y = 19 * (i / 5);
				parent.getMinecraft().textureManager.bindTexture(texture);
				drawButton(mouseX, mouseY, ms, x, y, 3);
				parent.getMinecraft().textureManager.bindTexture(icon);
				blit(ms, this.x + x, this.y + y, 0, 0, 16, 16, 16, 16);
				i++;
			}
		} else {
			Streams.zip(slots.stream(), icons.stream(), Pair::of).forEach(elem -> {
				int i = elem.getLeft();
				int x = px[i] + 46, y = py[i] + 46;
				parent.getMinecraft().textureManager.bindTexture(texture);
				drawButton(mouseX, mouseY, ms, x, y, 3);
				parent.getMinecraft().textureManager.bindTexture(elem.getRight());
				blit(ms, this.x + x, this.y + y, 0, 0, 16, 16, 16, 16);
			});
		}
	}
	
	@Override
	public void onClick(double mx, double my) {
		List<Integer> slots = holder.getSlots();
		if (slots.stream().max(Integer::compare).get() >= 12 || slots.stream().min(Integer::compare).get() < 0) {
			int i = 0;
			for (int slot : slots) {
				int x = 19 * (i % 5), y = 19 * (i / 5);
				if (clickButton(mx, my, x, y)) select(slot);
				i++;
			}
		} else {
			for (int slot : slots) {
				int x = px[slot] + 46, y = py[slot] + 46;
				if (clickButton(mx, my, x, y)) select(slot);
			}
		}
		hide();
	}
	
	public void show() {
		visible = active = true;
		transfer.active = false;
	}
	
	public void hide() {
		visible = active = false;
		transfer.active = true;
	}
	
	void select(int slot) {
		Messages.channel.sendToServer(new ProgramTransferSlotMessage(holder.getPosition(), slot));
	}
	
	void drawButton(int mx, int my, MatrixStack ms, int x, int y, int id) {
		blit(ms, this.x + x, this.y + y, 16 * id, highlight(mx, my, x, y, 16, 16), 16, 16, 64, 32);
	}
	
	boolean clickButton(double mx, double my, int x, int y) {
		return checkMouse(mx, my, x, y, 16, 16);
	}
	
	int highlight(int mx, int my, int x, int y, int w, int h) {
		return checkMouse(mx, my, x, y, w, h) ? 16 : 0;
	}
	
	boolean checkMouse(double mx, double my, int x, int y, int w, int h) {
		return mx >= this.x + x && mx < this.x + x + w && my >= this.y + y && my < this.y + y + h;
	}
	
}
