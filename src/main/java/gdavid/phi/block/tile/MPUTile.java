package gdavid.phi.block.tile;

import java.util.Set;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import gdavid.phi.block.MPUBlock;
import gdavid.phi.item.MPUCAD;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;

public class MPUTile extends TileEntity implements ITickableTileEntity {
	
	public static TileEntityType<?> type;
	
	public static final int efficiency = 30;
	public static final int potency = 80;
	public static final int complexityPerTick = 5;
	
	public static final String tagSpell = "spell";
	public static final String tagPsi = "psi";
	public static final String tagNext = "next";
	
	public Spell spell;
	public int psi;
	
	public MPUCaster fakePlayer;
	public ItemStack fakeCad = new ItemStack(MPUCAD.instance);
	
	public SpellContext context;
	public int complexityProcessed;
	
	public int nextX, nextY;
	
	public boolean spellChanged;
	
	public MPUTile() {
		super(type);
	}
	
	public void setSpell(Spell to) {
		spell = to.copy();
		spell.uuid = UUID.randomUUID();
		spellChanged = true;
		markDirty();
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void tick() {
		if (world.isRemote) return;
		if (spell == null) return;
		if (fakePlayer == null) fakePlayer = new MPUCaster();
		fakePlayer.fix();
		boolean recast = context == null;
		if (!recast) {
			try {
				recast = !((Set<SpellContext>) Class.forName("vazkii.psi.common.core.handler.PlayerDataHandler").getField("delayedContexts").get(null)).contains(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (recast) {
			context = new SpellContext().setPlayer(fakePlayer).setSpell(spell);
			if (!context.isValid()) return;
			if (!context.cspell.metadata.evaluateAgainst(fakeCad)) return;
			int cost = Math.max(0, context.cspell.metadata.getStat(EnumSpellStat.COST) * 100 / efficiency);
			if (psi < cost) return;
			if (cost != 0) {
				psi -= cost;
				markDirty();
			}
			complexityProcessed = 0;
			context.cspell.safeExecute(context);
			world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
		} else if (context != null && context.actions != null && !context.actions.isEmpty()) {
			SpellPiece piece = context.actions.peek().piece;
			if (piece.x != nextX || piece.y != nextY) {
				nextX = piece.x + 1;
				nextY = piece.y + 1;
				world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
			}
		}
	}
	
	public int complexityDelay(int complexity) {
		complexityProcessed += complexity;
		int delay = complexityProcessed / complexityPerTick;
		complexityProcessed %= complexityPerTick;
		return delay;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		read(nbt);
	}
	
	public void read(CompoundNBT nbt) {
		if (nbt.contains(tagSpell)) {
			if (spell == null) spell = Spell.createFromNBT(nbt.getCompound(tagSpell));
			else spell.readFromNBT(nbt.getCompound(tagSpell));
		}
		psi = nbt.getInt(tagPsi);
		nextX = nbt.getInt(tagNext + ".x");
		nextY = nbt.getInt(tagNext + ".y");
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		CompoundNBT spellNbt = new CompoundNBT();
		if (spell != null) spell.writeToNBT(spellNbt);
		nbt.put(tagSpell, spellNbt);
		nbt.putInt(tagPsi, psi);
		return nbt;
	}
	
	public CompoundNBT writePacket(CompoundNBT nbt) {
		if (spellChanged) {
			CompoundNBT spellNbt = new CompoundNBT();
			if (spell != null) spell.writeToNBT(spellNbt);
			nbt.put(tagSpell, spellNbt);
		}
		nbt.putInt(tagPsi, psi);
		if (context != null && context.actions != null && !context.actions.isEmpty()) {
			SpellPiece piece = context.actions.peek().piece;
			nbt.putInt(tagNext + ".x", piece.x + 1);
			nbt.putInt(tagNext + ".y", piece.y + 1);
		}
		return nbt;
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		SUpdateTileEntityPacket packet = new SUpdateTileEntityPacket(getPos(), 0, writePacket(new CompoundNBT()));
		spellChanged = false;
		return packet;
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
			super((ServerWorld) MPUTile.this.world, new GameProfile(new UUID(0, 0), "MPU"));
			connection = new ServerPlayNetHandler(server, new NetworkManager(PacketDirection.SERVERBOUND) {
				@Override public void sendPacket(IPacket<?> packet, GenericFutureListener<? extends Future<? super Void>> gfl) {}
			}, this);
			inventory.mainInventory.set(0, fakeCad);
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
			float yaw = world.getBlockState(pos).get(MPUBlock.HORIZONTAL_FACING).getHorizontalAngle();
			setPositionAndRotation(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, yaw, 0);
			rotationYawHead = rotationYaw;
		}
		
		public void complexityDelay(SpellContext context, int complexity) {
			int delay = MPUTile.this.complexityDelay(complexity);
			if (context.delay > 0) complexityProcessed = 0;
			context.delay += delay;
		}
		
	}
	
}
