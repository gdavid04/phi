package gdavid.phi.util;

import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.SpellPiece;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Caches a value so it's only computed once for a spell.
 * This is useful for things that are expensive to compute but only change when the spell changes, such as loop checks.
 */
public class SpellCachedView<T> {
	
	private final SpellPiece owner;
	private UUID lastSpellUUID = null;
	private final Supplier<? extends T> supplier, fallbackSupplier;
	private boolean updating = false;
	private T value;
	
	public SpellCachedView(@NotNull SpellPiece owner, @NotNull Supplier<? extends T> supplier) {
		this(owner, supplier, () -> { throw new SelfReferentialException(owner); });
	}
	
	public SpellCachedView(@NotNull SpellPiece owner, @NotNull Supplier<? extends T> supplier, @NotNull Supplier<? extends T> fallbackSupplier) {
		this.owner = owner;
		this.supplier = supplier;
		this.fallbackSupplier = fallbackSupplier;
	}
	
	public T get() throws SelfReferentialException {
		if (lastSpellUUID == null || !lastSpellUUID.equals(owner.spell.uuid)) {
			// Return a fallback value in case of self-reference
			if (updating) return fallbackSupplier.get();
			updating = true;
			value = supplier.get();
			lastSpellUUID = owner.spell.uuid;
			updating = false;
		}
		return value;
	}
	
	public static class SelfReferentialException extends IllegalStateException {
		
		public final SpellPiece piece;
		
		public SelfReferentialException(SpellPiece owner) {
			super("Self-referential SpellCachedView evaluation in " + owner);
			piece = owner;
		}
		
	}
	
}
