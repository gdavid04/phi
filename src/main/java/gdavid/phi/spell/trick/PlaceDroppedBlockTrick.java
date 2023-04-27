package gdavid.phi.spell.trick;

import gdavid.phi.spell.Errors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceTrick;

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
		BlockPos pos = targetVal.getPosition();
		if (!(context.focalPoint.world.isBlockLoaded(pos) && context.focalPoint.world.isBlockModifiable(context.caster, pos))) return null;
		BlockState state = context.focalPoint.world.getBlockState(pos);
		EntityPlaceEvent event = new EntityPlaceEvent(BlockSnapshot.create(context.focalPoint.world.getDimensionKey(), context.focalPoint.world, pos), context.focalPoint.world.getBlockState(pos.up()), context.caster);
		MinecraftForge.EVENT_BUS.post(event);
		if (!(state.isAir(context.focalPoint.world, pos) || state.getMaterial().isReplaceable()) || event.isCanceled()) return null;
		ItemEntity item = (ItemEntity) targetVal;
		ItemStack stack = item.getItem().copy();
		if (!(stack.getItem() instanceof BlockItem)) return null;
		ItemStack placed = stack.split(1);
		BlockItem blockItem = (BlockItem) placed.getItem();
		BlockRayTraceResult hit = new BlockRayTraceResult(Vector3d.ZERO, Direction.UP, pos, false);
		BlockItemUseContext ctx = new BlockItemUseContext(context.focalPoint.world, context.caster, Hand.MAIN_HAND, placed, hit);
		ActionResultType result = blockItem.tryPlace(ctx);
		if (result == ActionResultType.FAIL) return null;
		if (stack.isEmpty()) item.remove();
		else item.setItem(stack);
		context.focalPoint.world.playEvent(2001, pos, Block.getStateId(context.focalPoint.world.getBlockState(pos)));
		return null;
	}
	
}
