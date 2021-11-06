package gdavid.phi.block.tile;

import com.mojang.authlib.GameProfile;
import gdavid.phi.block.MPUBlock;
import gdavid.phi.item.MPUCAD;
import gdavid.phi.spell.trick.PsiTransferTrick;
import gdavid.phi.spell.trick.evaluation.ReevaluateTrick;
import gdavid.phi.spell.trick.marker.MoveMarkerTrick;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.lang.reflect.Field;
import java.util.Set;
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
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellPiece;

public class MPUTile extends TileEntity implements ITickableTileEntity {
	
	public static TileEntityType<MPUTile> type;
	
	public static final int complexityPerTick = 5;
	
	public static final ITextComponent statError = new TranslationTextComponent("psimisc.weak_cad");
	
	public static final String tagSpell = "spell";
	public static final String tagPsi = "psi";
	public static final String tagMessage = "message";
	public static final String tagComparatorSignal = "comparator_signal";
	public static final String tagCad = "cad";
	
	public Spell spell;
	public int psi;
	public ITextComponent message;
	public int comparatorSignal;
	
	public MPUCaster caster;
	public ItemStack cad = new ItemStack(MPUCAD.instance);
	
	public SpellContext context;
	public int castDelay;
	
	public static final String tagPrevPsi = "prev_psi";
	
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
		markDirty();
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
	}
	
	public void addPsi(int amount) {
		psi = Math.min(getPsiCapacity(), psi + amount);
		markDirty();
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
	}
	
	public int getPsiCapacity() {
		return 1000;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void tick() {
		if (psi < prevPsi) prevPsi = Math.max(psi, prevPsi - 25);
		else prevPsi = psi;
		if (world.isRemote) return;
		MPUCAD.instance.incrementTime(cad);
		if (spell == null) return;
		if (caster == null) caster = new MPUCaster();
		caster.fix();
		if (castDelay > 0) {
			castDelay--;
			return;
		}
		boolean recast = context == null;
		if (!recast) {
			try {
				recast = !((Set<SpellContext>) Class.forName("vazkii.psi.common.core.handler.PlayerDataHandler")
						.getField("delayedContexts").get(null)).contains(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (recast) {
			if (world.isBlockPowered(getPos())) return;
			context = new SpellContext().setPlayer(caster).setSpell(spell);
			if (!context.isValid()) return;
			if (!context.cspell.metadata.evaluateAgainst(cad)) {
				if (message != statError) {
					message = statError;
					markDirty();
					world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
				}
				return;
			} else if (message == statError) {
				message = null;
				markDirty();
				world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
			}
			int cost = context.cspell.metadata.getStat(EnumSpellStat.COST);
			if (cost == 0 && minCostFix(spell)) cost = 1;
			if (psi < cost) return;
			if (cost != 0) {
				psi -= cost;
				markDirty();
				world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
			}
			castDelay = context.cspell.metadata.getStat(EnumSpellStat.COMPLEXITY) / complexityPerTick;
			if (context.cspell.metadata.getFlag(PsiTransferTrick.flag)) castDelay = Math.max(castDelay, 4);
			context.cspell.safeExecute(context);
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
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		read(nbt);
	}
	
	public void read(CompoundNBT nbt) {
		if (spell == null) spell = Spell.createFromNBT(nbt.getCompound(tagSpell));
		else spell.readFromNBT(nbt.getCompound(tagSpell));
		psi = nbt.getInt(tagPsi);
		prevPsi = nbt.getInt(tagPrevPsi);
		MPUCAD.instance.getData(cad).deserializeNBT(nbt.getCompound(tagCad));
		message = ITextComponent.Serializer.getComponentFromJson(nbt.getString(tagMessage));
		comparatorSignal = nbt.getInt(tagComparatorSignal);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		CompoundNBT spellNbt = new CompoundNBT();
		if (spell != null) spell.writeToNBT(spellNbt);
		nbt.put(tagSpell, spellNbt);
		nbt.putInt(tagPsi, psi);
		nbt.putInt(tagPrevPsi, prevPsi);
		nbt.put(tagCad, MPUCAD.instance.getData(cad).serializeNBT());
		nbt.putString(tagMessage, ITextComponent.Serializer.toJson(message));
		nbt.putInt(tagComparatorSignal, comparatorSignal);
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
				Field eyeHeight = Entity.class.getDeclaredField("eyeHeight");
				eyeHeight.setAccessible(true);
				eyeHeight.setFloat(this, 0);
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
			psi -= amount;
			if (psi < 0) psi = 0;
			castDelay += cd;
			markDirty();
			MPUTile.this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
		}
		
		public int getPsi() {
			return psi;
		}
		
		public Integer getMaxPsi() {
			return getPsiCapacity();
		}
		
		public void setComparatorSignal(int value) {
			comparatorSignal = Math.max(Math.min(value, 15), 0);
			markDirty();
			MPUTile.this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
		}
		
	}
	
}
