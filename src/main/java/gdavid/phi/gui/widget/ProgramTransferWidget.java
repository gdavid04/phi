package gdavid.phi.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;

import gdavid.phi.Phi;
import gdavid.phi.block.tile.CADHolderTile;
import gdavid.phi.network.Messages;
import gdavid.phi.network.ProgramTransferMessage;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.client.gui.GuiProgrammer;

@OnlyIn(Dist.CLIENT)
public class ProgramTransferWidget extends Widget {
	
	static final ResourceLocation texture = new ResourceLocation(Phi.modId, "textures/gui/program_transfer.png");
	
	final GuiProgrammer parent;
	final CADHolderTile holder;
	final Direction dir;
	
	public ProgramTransferWidget(GuiProgrammer parent, CADHolderTile holder, boolean side, Direction dir) {
		super(parent.left + (side ? parent.xSize - 35 : 0), parent.top + parent.ySize + 12, 35, 16, StringTextComponent.EMPTY);
		this.parent = parent;
		this.holder = holder;
		this.dir = dir;
	}
	
	@Override
	@SuppressWarnings("resource")
	public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partial) {
		parent.getMinecraft().textureManager.bindTexture(texture);
		blit(ms, x, y, 0, checkMouse(mouseX, mouseY, 0, 0, 16, 16) ? 16 : 0, 16, 16, 32, 32);
		blit(ms, x + 19, y, 16, checkMouse(mouseX, mouseY, 19, 0, 16, 16) ? 16 : 0, 16, 16, 32, 32);
	}
	
	@Override
	public void onClick(double x, double y) {
		ProgramTransferMessage message;
		if (checkMouse(x, y, 0, 0, 16, 16)) {
			message = new ProgramTransferMessage(parent.programmer.getPos(), dir);
		} else if (checkMouse(x, y, 19, 0, 16, 16)) {
			message = new ProgramTransferMessage(holder.getPos(), dir.getOpposite());
		} else return;
		Messages.channel.sendToServer(message);
	}
	
	boolean checkMouse(int mx, int my, int x, int y, int w, int h) {
		return mx >= this.x + x && mx < this.x + x + w && my >= this.y + y && my < this.y + y + h;
	}
	
	boolean checkMouse(double mx, double my, int x, int y, int w, int h) {
		return mx >= this.x + x && mx < this.x + x + w && my >= this.y + y && my < this.y + y + h;
	}
	
}
