package gdavid.phi.block.tile;

import com.mojang.authlib.GameProfile;
import gdavid.phi.block.MPUBlock;
import gdavid.phi.item.MPUCAD;
import gdavid.phi.spell.trick.evaluation.ReevaluateTrick;
import gdavid.phi.spell.trick.marker.MoveMarkerTrick;
import gdavid.phi.spell.trick.mpu.PsiTransferTrick;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.lang.ref.WeakReference;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.spell.trick.PieceTrickParticleTrail;
import vazkii.psi.common.spell.trick.PieceTrickPlaySound;

public class MPUTile extends TileEntity implements ITickableTileEntity {
	
	public static TileEntityType<MPUTile> type;
	
	public static final int complexityPerTick = 5;
	
	public static final ITextComponent statError = new TranslationTextComponent("psimisc.weak_cad");
	
	public static final String tagSpell = "spell";
	public static final String tagPsi = "psi";
	public static final String tagMessage = "message";
	public static final String tagComparatorSignal = "comparator_signal";
	public static final String tagSuccessCount = "success_count";
	public static final String tagCad = "cad";
	
	public Spell spell;
	public int psi;
	public ITextComponent message;
	public int comparatorSignal;
	public int successCount;
	
	public MPUCaster caster;
	public ItemStack cad = new ItemStack(MPUCAD.instance);
	
	public WeakReference<SpellContext> context;
	public int castDelay;
	
	public int prevPsi;
	
	public MPUTile() {
		super(type);
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
		markDirty();
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
	}
	
	public void addPsi(int amount) {
		if (amount == 0) return;
		psi = Math.max(0, Math.min(getPsiCapacity(), psi + amount));
		markDirty();
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
	}
	
	public int getPsiCapacity() {
		return 1000;
	}
	
	public void setTime(int time) {
		MPUCAD.instance.setTime(cad, time);
		markDirty();
	}
	
	public void waveImpact(Float frequency, float focus) {
		addPsi(-Math.round(frequency * focus * 4));
		castDelay = Math.round(frequency * focus * 4);
	}
	
	@Override
	public void tick() {
		if (world.isRemote) {
			if (psi < prevPsi) prevPsi = Math.max(psi, prevPsi - 25);
			else prevPsi = psi;
			return;
		}
		// TODO save CAD data changes when not casting
		MPUCAD.instance.incrementTime(cad);
		if (spell == null) return;
		if (caster == null) caster = new MPUCaster();
		caster.fix();
		if (castDelay > 0) {
			castDelay--;
			return;
		}
		boolean recast = context == null || context.get() == null
				|| !PlayerDataHandler.delayedContexts.contains(context.get());
		if (recast) {
			if (world.isBlockPowered(getPos())) return;
			SpellContext ctx = new SpellContext().setPlayer(caster).setSpell(spell);
			context = new WeakReference<>(ctx);
			if (!ctx.isValid()) return;
			if (!ctx.cspell.metadata.evaluateAgainst(cad)) {
				if (message != statError) {
					message = statError;
					markDirty();
					world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
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
			markDirty();
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
						if (!(piece instanceof PieceTrickParticleTrail
								|| piece instanceof PieceTrickPlaySound
								|| piece instanceof MoveMarkerTrick
								|| piece instanceof ReevaluateTrick))
							return true;
					}
				} catch (SpellCompilationException e) {
				}
			}
		}
		return false;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		read(nbt);
	}
	
	public void read(CompoundNBT nbt) {
		if (spell == null) spell = Spell.createFromNBT(nbt.getCompound(tagSpell));
		else spell.readFromNBT(nbt.getCompound(tagSpell));
		psi = nbt.getInt(tagPsi);
		MPUCAD.instance.getData(cad).deserializeNBT(nbt.getCompound(tagCad));
		message = ITextComponent.Serializer.getComponentFromJson(nbt.getString(tagMessage));
		comparatorSignal = nbt.getInt(tagComparatorSignal);
		successCount = nbt.getInt(tagSuccessCount);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		CompoundNBT spellNbt = new CompoundNBT();
		if (spell != null) spell.writeToNBT(spellNbt);
		nbt.put(tagSpell, spellNbt);
		nbt.putInt(tagPsi, psi);
		nbt.put(tagCad, MPUCAD.instance.getData(cad).serializeNBT());
		nbt.putString(tagMessage, ITextComponent.Serializer.toJson(message));
		nbt.putInt(tagComparatorSignal, comparatorSignal);
		nbt.putInt(tagSuccessCount, successCount);
		return nbt;
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getPos(), 0, write(new CompoundNBT()));
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		read(packet.getNbtCompound());
	}
	
	public class MPUCaster extends FakePlayer {
		
		private MPUCaster() {
			super((ServerWorld) MPUTile.this.world, new GameProfile(UUID.randomUUID(), "MPU"));
			connection = new ServerPlayNetHandler(server, new NetworkManager(PacketDirection.SERVERBOUND) {
				
				@Override
				public void sendPacket(IPacket<?> packet, GenericFutureListener<? extends Future<? super Void>> gfl) {
				}
				
			}, this);
			inventory.mainInventory.set(0, cad);
			try {
				ObfuscationReflectionHelper.setPrivateValue(Entity.class, this, 0, "field_213326_aJ"); // eyeHeight
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public Vector3d getPositionVec() {
			return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		}
		
		@Override
		public BlockPos getPosition() {
			return pos;
		}
		
		@Override
		public Vector3d getLookVec() {
			return Vector3d.copy(getBlockState().get(MPUBlock.HORIZONTAL_FACING).getDirectionVec());
		}
		
		public void fix() {
			// MPU can't blink
			float yaw = getBlockState().get(MPUBlock.HORIZONTAL_FACING).getHorizontalAngle();
			setPositionAndRotation(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, yaw, 0);
			rotationYawHead = rotationYaw;
		}
		
		@Override
		public void sendMessage(ITextComponent component, UUID senderUUID) {
			message = component;
			markDirty();
			MPUTile.this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
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
			markDirty();
			MPUTile.this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
		}
		
		public void setTime(int time) {
			MPUTile.this.setTime(time);
		}
		
		public int getSuccessCount() {
			return successCount;
		}
		
		public void fail() {
			successCount = 0;
			markDirty();
		}
		
	}
	
}
