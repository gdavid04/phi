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

public class MPUTile extends TileEntity implements ITickableTileEntity {
	
	public static TileEntityType<MPUTile> type;
	
	public static final int complexityPerTick = 5;
	
	public static final String tagSpell = "spell";
	public static final String tagPsi = "psi";
	
	public Spell spell;
	public int psi;
	
	public MPUCaster fakePlayer;
	public ItemStack fakeCad = new ItemStack(MPUCAD.instance);
	
	public SpellContext context;
	public int castDelay;
	
	public MPUTile() {
		super(type);
	}
	
	public void setSpell(Spell to) {
		spell = to.copy();
		spell.uuid = UUID.randomUUID();
		markDirty();
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
	}
	
	@Override
	@SuppressWarnings("unchecked")
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
			if (castDelay > 0) {
				castDelay--;
				return;
			}
			context = new SpellContext().setPlayer(fakePlayer).setSpell(spell);
			if (!context.isValid()) return;
			if (!context.cspell.metadata.evaluateAgainst(fakeCad)) return;
			int cost = context.cspell.metadata.getStat(EnumSpellStat.COST);
			if (psi < cost) return;
			if (cost != 0) {
				psi -= cost;
				markDirty();
			}
			castDelay = context.cspell.metadata.getStat(EnumSpellStat.COMPLEXITY) / complexityPerTick;
			context.cspell.safeExecute(context);
			world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 18);
		}
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
			float yaw = getBlockState().get(MPUBlock.HORIZONTAL_FACING).getHorizontalAngle();
			setPositionAndRotation(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, yaw, 0);
			rotationYawHead = rotationYaw;
		}
		
	}
	
}
