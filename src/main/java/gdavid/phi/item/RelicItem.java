package gdavid.phi.item;

import java.util.List;
import com.google.common.collect.Lists;

import gdavid.phi.Phi;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.common.item.ItemCAD;

@EventBusSubscriber
public class RelicItem extends Item {
	
	public static final String tagSpell = "spell";
	
	public static final String tagTricks = "tricks";
	public static final String tagRegenLock = "regen_lock";
	
	public static final String tagEfficiency = "efficiency";
	public static final String tagPotency = "potency";
	public static final String tagComplexity = "complexity";
	public static final String tagBandwidth = "bandwidth";
	
	public final String id;
	
	public RelicItem(String id) {
		super(new Properties().maxStackSize(1).group(ItemGroup.MISC).rarity(Rarity.EPIC)); // TODO Phi creative tab
		setRegistryName(id);
		this.id = id;
	}
	
	public int getStat(ItemStack item, EnumCADStat stat) {
		CompoundNBT nbt = item.getOrCreateTag();
		switch (stat) {
		case EFFICIENCY: return nbt.getInt(tagEfficiency);
		case POTENCY: return nbt.getInt(tagPotency);
		case COMPLEXITY: return nbt.getInt(tagComplexity);
		case BANDWIDTH: return nbt.getInt(tagBandwidth);
		default: return 0;
		}
	}
	
	public Spell getSpell(ItemStack item) {
		return Spell.createFromNBT(item.getOrCreateChildTag(tagSpell));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack item, World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		CompoundNBT nbt = item.getOrCreateTag();
		tooltip.add(new TranslationTextComponent("item." + Phi.modId + "." + id + ".desc"));
		for (EnumCADStat stat : new EnumCADStat[] { EnumCADStat.EFFICIENCY, EnumCADStat.POTENCY,
				EnumCADStat.COMPLEXITY, EnumCADStat.BANDWIDTH }) {
			int value = getStat(item, stat);
			tooltip.add(new StringTextComponent(" ")
					.append(new TranslationTextComponent(stat.getName()).mergeStyle(TextFormatting.AQUA))
					.appendString(": " + (value == -1 ? "\u221E" : value)));
		}
		tooltip.add(new StringTextComponent(" ")
				.append(new TranslationTextComponent("item." + Phi.modId + "." + id + ".regen_lock").mergeStyle(TextFormatting.AQUA))
				.appendString(": " + nbt.getInt(tagRegenLock)));
		tooltip.add(new StringTextComponent(" ")
				.append(new TranslationTextComponent("item." + Phi.modId + "." + id + ".tricks").mergeStyle(TextFormatting.AQUA))
				.appendString(":"));
		ListNBT tricks = nbt.getList(tagTricks, Constants.NBT.TAG_STRING);
		for (INBT trick : tricks) { // TODO use map instead of repetition
			CompoundNBT trickNbt = new CompoundNBT();
			trickNbt.put("key", trick);
			String name;
			try {
				name = SpellPiece.createFromNBT(null, trickNbt).getUnlocalizedName();
			} catch (Exception e) {
				// just in case a piece requires a non null Spell
				ResourceLocation id = new ResourceLocation(((StringNBT) trick).getString());
				name = id.getNamespace() + ".spellpiece." + id.getPath();
			}
			tooltip.add(new StringTextComponent(" - ").append(new TranslationTextComponent(name)));
		}
		Spell spell = getSpell(item);
		tooltip.add(new StringTextComponent(" ")
				.append(new TranslationTextComponent(Phi.modId + ".spell").mergeStyle(TextFormatting.LIGHT_PURPLE))
				.appendString(": " + (spell == null ? "None" : spell.name)));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack item = player.getHeldItem(hand);
		CompoundNBT nbt = item.getOrCreateTag();
		// TODO reprogram
		// relics can cast regardless of overflow
		if (!ItemCAD.isTruePlayer(player)) return ActionResult.resultFail(item);
		CooldownTracker cooldown = player.getCooldownTracker();
		if (cooldown.hasCooldown(this)) return ActionResult.resultFail(item);
		Spell spell = getSpell(item);
		if (spell == null) return ActionResult.resultFail(item);
		List<String> tricks = Lists.newArrayList(nbt.getList(tagTricks, Constants.NBT.TAG_STRING)
				.stream().map(INBT::getString).toArray(String[]::new));
		for (SpellPiece[] row : spell.grid.gridData) {
			for (SpellPiece piece : row) {
				if (piece == null || piece.getPieceType() != EnumPieceType.TRICK) continue;
				if (!tricks.remove(piece.registryKey.toString())) {
					if (!world.isRemote) player.sendMessage(new TranslationTextComponent("item." + Phi.modId + "." + id + ".trick_error").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
					return ActionResult.resultFail(item);
				}
			}
		}
		SpellContext ctx = new SpellContext().setPlayer(player).setSpell(spell);
		ctx.castFrom = hand;
		if (!ctx.isValid()) return ActionResult.resultFail(item);
		for (EnumSpellStat stat : EnumSpellStat.values()) {
			if (stat.getTarget() == null || stat == EnumSpellStat.PROJECTION) continue;
			int value = getStat(item, stat.getTarget());
			if (value != -1 && value < ctx.cspell.metadata.getStat(stat)) {
				if (!world.isRemote) player.sendMessage(new TranslationTextComponent("item." + Phi.modId + "." + id + ".stat_error").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
				return ActionResult.resultFail(item);
			}
		}
		int efficiency = getStat(item, EnumCADStat.EFFICIENCY);
		int regenLock = nbt.getInt(tagRegenLock);
		if (efficiency != -1) {
			int cost = ctx.cspell.metadata.getStat(EnumSpellStat.COST);
			if (efficiency != 0) cost = (int) (cost * 100.0 / efficiency);
			if (cost > 0) {
				IPlayerData playerData = PsiAPI.internalHandler.getDataForPlayer(player);
				if (playerData.isOverflowed()) cooldown.setCooldown(this, regenLock);
				playerData.deductPsi(cost, regenLock, true, false);
			}
		}
		if (!world.isRemote) ctx.cspell.safeExecute(ctx);
		return ActionResult.resultSuccess(item);
	}
	
}
