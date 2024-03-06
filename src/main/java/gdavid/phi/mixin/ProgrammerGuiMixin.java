package gdavid.phi.mixin;

import gdavid.phi.gui.widget.ProgramTransferWidget;
import gdavid.phi.util.IProgramTransferTarget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
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
	
	private ProgrammerGuiMixin(Component p_i51108_1_) {
		super(p_i51108_1_);
	}
	
	@Inject(method = "init", at = @At("RETURN"), remap = true)
	private void init(CallbackInfo callback) {
		if (programmer == null) return;
		GuiProgrammer self = (GuiProgrammer) (Object) this;
		Level world = programmer.getLevel();
		BlockPos pos = programmer.getBlockPos();
		Direction dir = programmer.getBlockState().getValue(BlockProgrammer.FACING);
		BlockEntity left = world.getBlockEntity(pos.relative(dir.getClockWise()));
		if (left instanceof IProgramTransferTarget) {
			ProgramTransferWidget transfer = new ProgramTransferWidget(self, (IProgramTransferTarget) left, false,
					dir.getClockWise());
			addWidget(transfer);
			addWidget(transfer.select);
		}
		BlockEntity right = world.getBlockEntity(pos.relative(dir.getCounterClockWise()));
		if (right instanceof IProgramTransferTarget) {
			ProgramTransferWidget transfer = new ProgramTransferWidget(self, (IProgramTransferTarget) right, true,
					dir.getCounterClockWise());
			addWidget(transfer);
			addWidget(transfer.select);
		}
	}
	
}
