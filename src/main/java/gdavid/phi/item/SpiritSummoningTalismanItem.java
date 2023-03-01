package gdavid.phi.item;

import gdavid.phi.Phi;
import gdavid.phi.entity.SpiritEntity;
import gdavid.phi.util.RenderHelper;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.common.Psi;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;

public class SpiritSummoningTalismanItem extends Item {
	
	public final String id;
	
	static final String tagUuid = "uuid";
	
	public SpiritSummoningTalismanItem(String id) {
		super(new Properties().maxStackSize(1).group(ItemGroup.MISC));
		setRegistryName(id);
		this.id = id;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack item, World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		tooltip.add(new TranslationTextComponent("item." + Phi.modId + "." + id + ".desc"));
	}
	
	public int getMinDuration(ItemStack stack) {
		return 20 * 2;
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return 20 * 7 + 1;
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack item = player.getHeldItem(hand);
		if (world instanceof ServerWorld) {
			SpiritEntity spirit = getSpirit(item, (ServerWorld) world);
			if (spirit != null && spirit.ticksExisted < 60) return ActionResult.resultPass(item);
			player.setActiveHand(hand);
		}
		return ActionResult.resultSuccess(item);
	}
	
	@Override
	public void onUse(World world, LivingEntity player, ItemStack stack, int count) {
		PlayerData data = PlayerDataHandler.get((PlayerEntity) player);
		if (data.overflowed) {
			player.stopActiveHand();
			return;
		}
		data.deductPsi(25, 20, true);
		if (world.isRemote) {
			Vector3d pos = player.getPositionVec().add(player.getLookVec().scale(1.5)).add(0, player.getEyeHeight(), 0);
			ItemStack cad = PsiAPI.getPlayerCAD((PlayerEntity) player);
			int color = ICADColorizer.DEFAULT_SPELL_COLOR;
			if (!cad.isEmpty()) {
				ItemStack colorizer = ((ICAD) cad.getItem()).getComponentInSlot(cad, EnumCADComponent.DYE);
				color = RenderHelper.getColorForColorizer(colorizer);
			}
			int r = RenderHelper.r(color);
			int g = RenderHelper.g(color);
			int b = RenderHelper.b(color);
			if (getUseDuration(stack) - count >= getMinDuration(stack)) {
				Psi.proxy.sparkleFX(pos.x, pos.y, pos.z, r / 2, g / 2, b / 2, 0, 0, 0, 2.75f, 15);
			}
			Vector3d dir = new Vector3d(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).scale(0.1);
			pos = pos.subtract(dir.scale(10));
			Psi.proxy.sparkleFX(pos.x, pos.y, pos.z, r, g, b, (float) dir.x, (float) dir.y, (float) dir.z, 1.2f, 15);
		}
		if (count == 1) player.stopActiveHand();
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack item, World world, LivingEntity player, int timeLeft) {
		int duration = getUseDuration(item) - timeLeft;
		if (duration < getMinDuration(item)) {
			PlayerDataHandler.get((PlayerEntity) player).deductPsi(-25 * duration, 0, true);
			return;
		}
		if (!(world instanceof ServerWorld)) return;
		SpiritEntity spirit = new SpiritEntity(world, player, (duration - 20) * 150);
		Vector3d pos = player.getPositionVec().add(player.getLookVec().scale(1.5)).add(0, player.getEyeHeight() - 0.25,
				0);
		spirit.setPosition(pos.x, pos.y, pos.z);
		world.addEntity(spirit);
		setSpirit(item, (ServerWorld) world, spirit);
	}
	
	public SpiritEntity getSpirit(ItemStack item, ServerWorld world) {
		if (!item.getOrCreateTag().contains(tagUuid)) return null;
		CompoundNBT nbt = item.getOrCreateTag();
		Entity entity = world.getEntityByUuid(nbt.getUniqueId(tagUuid));
		if (entity instanceof SpiritEntity) return (SpiritEntity) entity;
		return null;
	}
	
	public void setSpirit(ItemStack item, ServerWorld world, SpiritEntity spirit) {
		SpiritEntity prev = getSpirit(item, world);
		if (prev != null) prev.remove();
		item.getOrCreateTag().putUniqueId(tagUuid, spirit.getUniqueID());
	}
	
}
