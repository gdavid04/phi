package gdavid.phi.item;

import gdavid.phi.Phi;
import gdavid.phi.entity.SpiritEntity;
import gdavid.phi.util.RenderHelper;
import java.util.List;

import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.common.Psi;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;

import net.minecraft.world.item.Item.Properties;

import static net.minecraft.world.entity.Entity.RemovalReason.DISCARDED;

public class SpiritSummoningTalismanItem extends Item {
	
	public final String id;
	
	static final String tagUuid = "uuid";
	
	public SpiritSummoningTalismanItem(String id) {
		super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
		this.id = id;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack item, Level world, List<Component> tooltip, TooltipFlag advanced) {
		tooltip.add(Component.translatable("item." + Phi.modId + "." + id + ".desc"));
	}
	
	public int getMinDuration(ItemStack stack) {
		return 20 * 2;
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return 20 * 7 + 1;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);
		if (world instanceof ServerLevel) {
			SpiritEntity spirit = getSpirit(item, (ServerLevel) world);
			if (spirit != null && spirit.tickCount < 60) return InteractionResultHolder.pass(item);
			player.startUsingItem(hand);
		}
		return InteractionResultHolder.success(item);
	}
	
	@Override
	public void onUseTick(Level world, LivingEntity player, ItemStack stack, int count) {
		PlayerData data = PlayerDataHandler.get((Player) player);
		if (data.overflowed) {
			player.releaseUsingItem();
			return;
		}
		data.deductPsi(25, 20, true);
		if (world.isClientSide) {
			Vec3 pos = player.position().add(player.getLookAngle().scale(1.5)).add(0, player.getEyeHeight(), 0);
			ItemStack cad = PsiAPI.getPlayerCAD((Player) player);
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
			Vec3 dir = new Vec3(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).scale(0.1);
			pos = pos.subtract(dir.scale(10));
			Psi.proxy.sparkleFX(pos.x, pos.y, pos.z, r, g, b, (float) dir.x, (float) dir.y, (float) dir.z, 1.2f, 15);
		}
		if (count == 1) player.releaseUsingItem();
	}
	
	@Override
	public void releaseUsing(ItemStack item, Level world, LivingEntity player, int timeLeft) {
		int duration = getUseDuration(item) - timeLeft;
		if (duration < getMinDuration(item)) {
			PlayerDataHandler.get((Player) player).deductPsi(-25 * duration, 0, true);
			return;
		}
		if (!(world instanceof ServerLevel)) return;
		SpiritEntity spirit = new SpiritEntity(world, player, (duration - 20) * 150);
		Vec3 pos = player.position().add(player.getLookAngle().scale(1.5)).add(0, player.getEyeHeight() - 0.25,
				0);
		spirit.setPos(pos.x, pos.y, pos.z);
		world.addFreshEntity(spirit);
		setSpirit(item, (ServerLevel) world, spirit);
	}
	
	public SpiritEntity getSpirit(ItemStack item, ServerLevel world) {
		if (!item.getOrCreateTag().contains(tagUuid)) return null;
		CompoundTag nbt = item.getOrCreateTag();
		Entity entity = world.getEntity(nbt.getUUID(tagUuid));
		if (entity instanceof SpiritEntity) return (SpiritEntity) entity;
		return null;
	}
	
	public void setSpirit(ItemStack item, ServerLevel world, SpiritEntity spirit) {
		SpiritEntity prev = getSpirit(item, world);
		if (prev != null) prev.discard();
		item.getOrCreateTag().putUUID(tagUuid, spirit.getUUID());
	}
	
}
