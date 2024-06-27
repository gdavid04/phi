package gdavid.phi.spell.trick;

import gdavid.phi.spell.Errors;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceTrick;

import static net.minecraft.world.entity.Entity.RemovalReason.DISCARDED;

public class PlaceDroppedBlockTrick extends PieceTrick {
	
	SpellParam<Entity> target;
	
	public PlaceDroppedBlockTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ParamEntity(SpellParam.GENERIC_NAME_TARGET, SpellParam.YELLOW, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		meta.addStat(EnumSpellStat.POTENCY, 6);
		meta.addStat(EnumSpellStat.COST, 6);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Entity targetVal = getNonnullParamValue(context, target);
		if (!(targetVal instanceof ItemEntity)) Errors.invalidTarget.runtime();
		BlockPos pos = targetVal.blockPosition();
		if (!(context.focalPoint.level.hasChunkAt(pos) && context.focalPoint.level.mayInteract(context.caster, pos))) return null;
		BlockState state = context.focalPoint.level.getBlockState(pos);
		EntityPlaceEvent event = new EntityPlaceEvent(BlockSnapshot.create(context.focalPoint.level.dimension(), context.focalPoint.level, pos), context.focalPoint.level.getBlockState(pos.above()), context.caster);
		MinecraftForge.EVENT_BUS.post(event);
		if (!(state.isAir() || state.getMaterial().isReplaceable()) || event.isCanceled()) return null;
		ItemEntity item = (ItemEntity) targetVal;
		ItemStack stack = item.getItem().copy();
		if (!(stack.getItem() instanceof BlockItem)) return null;
		ItemStack placed = stack.split(1);
		BlockItem blockItem = (BlockItem) placed.getItem();
		BlockHitResult hit = new BlockHitResult(Vec3.ZERO, Direction.UP, pos, false);
		BlockPlaceContext ctx = new BlockPlaceContext(context.focalPoint.level, context.caster, InteractionHand.MAIN_HAND, placed, hit);
		InteractionResult result = blockItem.place(ctx);
		if (result == InteractionResult.FAIL) return null;
		if (stack.isEmpty()) item.discard();
		else item.setItem(stack);
		context.focalPoint.level.levelEvent(2001, pos, Block.getId(context.focalPoint.level.getBlockState(pos)));
		return null;
	}
	
}
