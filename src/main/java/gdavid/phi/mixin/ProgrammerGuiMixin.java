package gdavid.phi.mixin;

import gdavid.phi.gui.widget.ProgramTransferWidget;
import gdavid.phi.util.IProgramTransferTarget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.psi.client.gui.GuiProgrammer;
import vazkii.psi.common.block.BlockProgrammer;
import vazkii.psi.common.block.tile.TileProgrammer;

@Mixin(value = GuiProgrammer.class, remap = false)
public class ProgrammerGuiMixin extends Screen {
	
	@Shadow
	@Final
	public TileProgrammer programmer;
	
	private ProgrammerGuiMixin(ITextComponent p_i51108_1_) {
		super(p_i51108_1_);
	}
	
	@Inject(method = "init", at = @At("RETURN"), remap = true)
	private void init(CallbackInfo callback) {
		if (programmer == null) return;
		GuiProgrammer self = (GuiProgrammer) (Object) this;
		World world = programmer.getWorld();
		BlockPos pos = programmer.getPos();
		Direction dir = programmer.getBlockState().get(BlockProgrammer.HORIZONTAL_FACING);
		TileEntity left = world.getTileEntity(pos.offset(dir.rotateY()));
		if (left instanceof IProgramTransferTarget) {
			ProgramTransferWidget transfer = new ProgramTransferWidget(self, (IProgramTransferTarget) left, false,
					dir.rotateY());
			addButton(transfer);
			addButton(transfer.select);
		}
		TileEntity right = world.getTileEntity(pos.offset(dir.rotateYCCW()));
		if (right instanceof IProgramTransferTarget) {
			ProgramTransferWidget transfer = new ProgramTransferWidget(self, (IProgramTransferTarget) right, true,
					dir.rotateYCCW());
			addButton(transfer);
			addButton(transfer.select);
		}
	}
	
}
