package gdavid.phi.spell.trick;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import gdavid.phi.util.ReferenceParam;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class RewriteVariableTrick extends PieceTrick {
	
	ReferenceParam target;
	SpellParam<Number> value;
	
	public RewriteVariableTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(target = new ReferenceParam(SpellParam.GENERIC_NAME_TARGET, SpellParam.RED, false) {
			
			@Override
			public boolean canAccept(SpellPiece piece) {
				return piece.getClass().getName().equals("vazkii.psi.common.spell.constant.PieceConstantNumber");
			}
			
		});
		addParam(value = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.BLUE, false, false));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		meta.addStat(EnumSpellStat.COMPLEXITY, 5);
	}
	
	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		try {
			SpellPiece targetPiece = spell.grid.getPieceAtSideWithRedirections(x, y, paramSides.get(target));
			CompoundNBT nbt = new CompoundNBT(), spellNbt = new CompoundNBT();
			targetPiece.writeToNBT(nbt);
			double val = getNonnullParamValue(context, value).doubleValue();
			int decimalPlaces = Math.max(0, 3 - (int) Math.floor(Math.log10(Math.abs(val))));
			if (val == 0) decimalPlaces = 3;
			if (val < 0) decimalPlaces--;
			nbt.putString("constantValue", new BigDecimal(val).setScale(decimalPlaces, RoundingMode.HALF_UP).toPlainString());
			targetPiece.readFromNBT(nbt);
			spell.uuid = UUID.randomUUID();
			spell.writeToNBT(spellNbt);
			ItemStack castFrom = context.tool.isEmpty() ? PsiAPI.getPlayerCAD(context.caster) : context.tool;
			ISocketable socketable = castFrom.getCapability(PsiAPI.SOCKETABLE_CAPABILITY).resolve().get();
			ItemStack bullet = socketable.getSelectedBullet();
			CompoundNBT bulletNbt = bullet.getTag();
			bulletNbt.put("spell", spellNbt);
		} catch (SpellCompilationException e) {}
		return null;
	}
	
}
