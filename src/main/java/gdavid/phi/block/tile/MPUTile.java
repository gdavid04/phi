package gdavid.phi.block.tile;

import com.mojang.authlib.GameProfile;
import gdavid.phi.block.MPUBlock;
import gdavid.phi.cable.CableNetwork;
import gdavid.phi.cable.ICableConnected;
import gdavid.phi.item.MPUCAD;
import gdavid.phi.spell.trick.evaluation.ReevaluateTrick;
import gdavid.phi.spell.trick.marker.MoveMarkerTrick;
import gdavid.phi.spell.trick.mpu.PsiTransferTrick;
import gdavid.phi.util.IProgramTransferTarget;
import gdavid.phi.util.IPsiAcceptor;
import gdavid.phi.util.IWaveImpacted;
import gdavid.phi.util.RedstoneMode;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.UUID;

import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingPlayerChatMessage;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellPiece;

public class MPUTile extends BlockEntity implements ICableConnected, IProgramTransferTarget, IWaveImpacted, IPsiAcceptor {
	
	public static BlockEntityType<MPUTile> type;
	
	public static final int complexityPerTick = 5;
	
	public static final Component statError = Component.translatable("psimisc.weak_cad");
	
	public static final String tagSpell = "spell";
	public static final String tagPsi = "psi";
	public static final String tagMessage = "message";
	public static final String tagNearbySpeech = "nearby_speech";
	public static final String tagComparatorSignal = "comparator_signal";
	public static final String tagSuccessCount = "success_count";
	public static final String tagRedstoneMode = "redstoneMode";
	public static final String tagCad = "cad";
	
	public Spell spell;
	public int psi;
	public Component message;
	public String nearbySpeech = "";
	public int comparatorSignal;
	public int successCount;
	public RedstoneMode redstoneMode = RedstoneMode.always;
	public boolean prevRedstoneSignal, redstoneSignal;
	
	public MPUCaster caster;
	public ItemStack cad = new ItemStack(MPUCAD.instance);
	
	public WeakReference<SpellContext> context;
	public int castDelay;
	
	public int prevPsi;
	
	public MPUTile(BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public BlockPos getPosition() {
		return worldPosition;
	}
	
	@Override
	public Spell getSpell() {
		return spell;
	}
	
	@Override
	public void setSpell(Player player, Spell spell) {
		setSpell(spell);
	}
	
	public void setSpell(Spell to) {
		if (to == null) {
			spell = null;
		} else {
			spell = to.copy();
			spell.uuid = UUID.randomUUID();
		}
		message = null;
		successCount = 0;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 18);
	}
	
	@Override
	public void addPsi(int amount) {
		if (amount == 0) return;
		psi = Math.max(0, Math.min(getPsiCapacity(), psi + amount));
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 18);
	}
	
	public int getPsiCapacity() {
		return 1000;
	}
	
	public void setTime(int time) {
		MPUCAD.instance.setTime(cad, time);
		setChanged();
	}
	
	@Override
	public void waveImpact(Float frequency, float focus) {
		addPsi(-Math.round(frequency * focus * 4));
		castDelay = Math.round(frequency * focus * 4);
	}
	
	public void setNearbySpeech(String to) {
		nearbySpeech = to;
		setChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void tick() {
		if (level.isClientSide) {
			if (psi < prevPsi) prevPsi = Math.max(psi, prevPsi - 25);
			else prevPsi = psi;
			return;
		}
		// TODO save CAD data changes when not casting
		MPUCAD.instance.incrementTime(cad);
		prevRedstoneSignal = redstoneSignal;
		redstoneSignal = level.hasNeighborSignal(getBlockPos());
		if (spell == null) return;
		if (caster == null) caster = new MPUCaster();
		caster.fix();
		if (castDelay > 0) {
			castDelay--;
			return;
		}
		if (!redstoneMode.isActive(prevRedstoneSignal, redstoneSignal)) return;
		boolean recast = context == null || context.get() == null;
		if (!recast) {
			try {
				recast = !((Set<SpellContext>) Class.forName("vazkii.psi.common.core.handler.PlayerDataHandler")
						.getField("delayedContexts").get(null)).contains(context.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (recast) {
			SpellContext ctx = new SpellContext().setPlayer(caster).setSpell(spell);
			context = new WeakReference<>(ctx);
			if (!ctx.isValid()) return;
			if (!ctx.cspell.metadata.evaluateAgainst(cad)) {
				if (message != statError) {
					message = statError;
					setChanged();
					level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 18);
				}
				return;
			}
			int cost = ctx.cspell.metadata.getStat(EnumSpellStat.COST);
			if (cost == 0 && minCostFix(spell)) cost = 1;
			if (psi < cost) return;
			addPsi(-cost);
			castDelay = ctx.cspell.metadata.getStat(EnumSpellStat.COMPLEXITY) / complexityPerTick;
			if (ctx.cspell.metadata.getFlag(PsiTransferTrick.flag)) castDelay = Math.max(castDelay, 4);
			ctx.cspell.safeExecute(ctx);
			successCount++;
			setChanged();
		}
	}
	
	public boolean minCostFix(Spell spell) {
		for (SpellPiece[] pieces : spell.grid.gridData) {
			for (SpellPiece piece : pieces) {
				if (piece == null || piece.getPieceType() != EnumPieceType.TRICK) continue;
				try {
					SpellMetadata meta = new SpellMetadata();
					piece.addToMetadata(meta);
					if (meta.getStat(EnumSpellStat.PROJECTION) != 0) {
						String name = piece.getClass().getName();
						if (!name.equals("vazkii.psi.common.spell.trick.PieceTrickParticleTrail")
								&& !name.equals("vazkii.psi.common.spell.trick.PieceTrickPlaySound")
								&& !(piece instanceof MoveMarkerTrick) && !(piece instanceof ReevaluateTrick))
							return true;
					}
				} catch (SpellCompilationException e) {
				}
			}
		}
		return false;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (spell == null) spell = Spell.createFromNBT(nbt.getCompound(tagSpell));
		else spell.readFromNBT(nbt.getCompound(tagSpell));
		psi = nbt.getInt(tagPsi);
		MPUCAD.instance.getData(cad).deserializeNBT(nbt.getCompound(tagCad));
		message = Component.Serializer.fromJson(nbt.getString(tagMessage));
		nearbySpeech = nbt.getString(tagNearbySpeech);
		comparatorSignal = nbt.getInt(tagComparatorSignal);
		successCount = nbt.getInt(tagSuccessCount);
		redstoneMode = RedstoneMode.values()[nbt.getInt(tagRedstoneMode)];
	}
	
	@Override
	public CompoundTag serializeNBT() {
		var nbt = super.serializeNBT();
		CompoundTag spellNbt = new CompoundTag();
		if (spell != null) spell.writeToNBT(spellNbt);
		nbt.put(tagSpell, spellNbt);
		nbt.putInt(tagPsi, psi);
		nbt.put(tagCad, MPUCAD.instance.getData(cad).serializeNBT());
		nbt.putString(tagMessage, Component.Serializer.toJson(message));
		nbt.putString(tagNearbySpeech, nearbySpeech);
		nbt.putInt(tagComparatorSignal, comparatorSignal);
		nbt.putInt(tagSuccessCount, successCount);
		nbt.putInt(tagRedstoneMode, redstoneMode.ordinal());
		return nbt;
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this, IForgeBlockEntity::serializeNBT);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		return serializeNBT();
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		load(packet.getTag());
	}
	
	@Override
	public boolean isController() {
		return false;
	}
	
	public class MPUCaster extends FakePlayer {
		
		private MPUCaster() {
			super((ServerLevel) MPUTile.this.level, new GameProfile(UUID.randomUUID(), "MPU"));
			connection = new ServerGamePacketListenerImpl(server, new Connection(PacketFlow.SERVERBOUND) {
				
				@Override
				public void send(Packet<?> packet, PacketSendListener listener) {}
				
			}, this);
			getInventory().items.set(0, cad);
			try {
				ObfuscationReflectionHelper.setPrivateValue(Entity.class, this, 0, "eyeHeight");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public Vec3 position() {
			return new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
		}
		
		@Override
		public BlockPos blockPosition() {
			return worldPosition;
		}
		
		@Override
		public Vec3 getLookAngle() {
			return Vec3.atLowerCornerOf(getFeetBlockState().getValue(MPUBlock.FACING).getNormal());
		}
		
		public void fix() {
			// MPU can't blink
			float yaw = getFeetBlockState().getValue(MPUBlock.FACING).toYRot();
			absMoveTo(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, yaw, 0);
			yHeadRot = getYRot();
		}
		
		@Override
		public void sendSystemMessage(Component component, boolean bypassHidden) {
			sendMessage(component);
		}
		
		@Override
		public void sendChatMessage(OutgoingPlayerChatMessage message, boolean filter, ChatType.Bound bound) {
			sendMessage(message.serverContent());
		}
		
		private void sendMessage(Component component) {
			message = component;
			setChanged();
			MPUTile.this.level.sendBlockUpdated(worldPosition, getFeetBlockState(), getFeetBlockState(), 18);
		}
		
		public void deductPsi(int amount, int cd) {
			addPsi(-amount);
			castDelay += cd;
		}
		
		public int getPsi() {
			return psi;
		}
		
		public int getMaxPsi() {
			return getPsiCapacity();
		}
		
		public void setComparatorSignal(int value) {
			comparatorSignal = Math.max(Math.min(value, 15), 0);
			setChanged();
			MPUTile.this.level.sendBlockUpdated(worldPosition, getFeetBlockState(), getFeetBlockState(), 3);
		}
		
		public void setTime(int time) {
			MPUTile.this.setTime(time);
		}
		
		public BlockPos getConnected(Direction side) {
			return CableNetwork.getController(level, worldPosition, side);
		}
		
		public int getSuccessCount() {
			return successCount;
		}
		
		public String getNearbySpeech() {
			return nearbySpeech;
		}
		
		public void fail() {
			successCount = 0;
			setChanged();
		}
		
	}
	
}
