package gdavid.phi.util;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public interface ICableConnected {
	
	/**
	 * Gets the connection the tile entity provides to the cable connected on the given side.
	 */
	public @Nullable Connection getController(Direction side);
	
	/**
	 * Returns whether the tile entity should connect to cables even if it's not the controller.
	 */
	public boolean isAcceptor(Direction side);
	
	public class Connection {
		
		public static final String tagPosition = "position";
		public static final String tagSide = "side";
		public static final String tagDistance = "distance";
		
		public final BlockPos pos;
		// TODO allow connections sideways up and down like redstone wire
		@Nullable public final Direction side;
		public final int distance;
		
		public Connection(BlockPos pos, @Nullable Direction side, int distance) {
			this.pos = pos;
			this.side = side;
			this.distance = distance;
		}
		
		public static Connection read(CompoundNBT nbt) {
			if (!nbt.contains(tagPosition)) return null;
			return new Connection(BlockPos.fromLong(nbt.getLong(tagPosition)),
					nbt.contains(tagSide) ? Direction.byIndex(nbt.getByte(tagSide)) : null,
					nbt.getInt(tagDistance));
		}

		public static CompoundNBT write(Connection c) {
			CompoundNBT nbt = new CompoundNBT();
			if (c != null) {
				nbt.putLong(tagPosition, c.pos.toLong());
				if (c.side != null) nbt.putByte(tagSide, (byte) c.side.getHorizontalIndex());
				nbt.putInt(tagDistance, c.distance);
			}
			return nbt;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof Connection)) return false;
			Connection other = (Connection) obj;
			return Objects.equals(pos, other.pos)
					&& side == other.side
					&& distance == other.distance;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(pos, side, distance);
		}
		
	}
	
}
