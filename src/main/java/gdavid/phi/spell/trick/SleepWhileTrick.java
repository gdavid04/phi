package gdavid.phi.spell.trick;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import gdavid.phi.Phi;
import gdavid.phi.api.ICustomCompile;
import gdavid.phi.spell.Param;
import gdavid.phi.util.ParamHelper;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.CompiledSpell.Action;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellParam.Side;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class SleepWhileTrick extends PieceTrick implements ICustomCompile {
	
	public static final String duration = Phi.modId + ":trick_sleep_while.duration";
	
	static final WeakHashMap<CompiledSpell, Map<SpellPiece, Action>> captureActions = new WeakHashMap<>();
	static final WeakHashMap<SpellContext, Stack<Action>> capturedStacks = new WeakHashMap<>();
	
	SpellParam<Number> condition, maximum, frequency;
	
	public SleepWhileTrick(Spell spell) {
		super(spell);
	}
	
	@Override
	public void initParams() {
		addParam(condition = new ParamNumber(Param.condition.name, SpellParam.BLUE, false, false));
		addParam(maximum = new ParamNumber(SpellParam.GENERIC_NAME_MAX, SpellParam.PURPLE, false, true));
		addParam(frequency = new ParamNumber(Param.frequency.name, SpellParam.CYAN, true, true));
	}
	
	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		float frequencyMultiplier = Math.max(1.1f, (20 - ParamHelper.positiveInt(this, frequency, 1)) * 0.25f);
		meta.addStat(EnumSpellStat.POTENCY, (int) Math.ceil(frequencyMultiplier * ParamHelper.positiveInt(this, maximum)));
	}
	
	@Override
	public void compile(CompiledSpell compiled, ICompilerCallback cb) throws SpellCompilationException {
		if (compiled.actionMap.containsKey(this)) {
			compiled.actions.remove(captureActions.get(compiled).get(this));
		} else {
			captureActions.computeIfAbsent(compiled, cspell -> new HashMap<>()).put(this, new CaptureAction(compiled));
		}
		ICustomCompile.super.compile(compiled, cb);
		ListIterator<Action> endIter = compiled.actions.listIterator(compiled.actions.size() - 1); // I hate the iterators in Java
		EnumSet<Side> usedSides = EnumSet.noneOf(Side.class);
		cb.build(cb.param(condition, usedSides));
		compiled.actions.add(captureActions.get(compiled).get(this));
		ListIterator<Action> iter = compiled.actions.listIterator(compiled.actions.size() - 1);
		SpellPiece piece = iter.previous().piece;
		while (!iter.equals(endIter)) { // hoist tricks outside repeating frame
			if (piece.getPieceType() == EnumPieceType.TRICK) cb.build(piece);
			piece = iter.previous().piece;
			if (piece == this) break; // why do I need this
		}
		cb.build(cb.param(maximum, usedSides));
		cb.buildOptional(cb.param(frequency, usedSides));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object execute(SpellContext context) throws SpellRuntimeException {
		int remaining = (int) context.customData.get(duration);
		int delay = Math.min(getParamValueOrDefault(context, frequency, 1).intValue(), remaining);
		if (remaining > 0 && Math.abs(getParamValue(context, condition).doubleValue()) < 1) {
			context.actions = (Stack<Action>) capturedStacks.get(context).clone(); // reevaluate condition
			context.delay = delay;
			context.customData.put(duration, remaining - delay);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void capture(SpellContext context) throws SpellRuntimeException {
		// capture action only runs once and isn't captured
		capturedStacks.put(context, (Stack<Action>) context.actions.clone());
		// maximum and frequency are already evaluated here, so they can be safely accessed
		context.customData.put(duration, getNonnullParamValue(context, maximum).intValue());
	}
	
	class CaptureAction extends Action {
		
		public CaptureAction(CompiledSpell cspell) {
			cspell.super(SleepWhileTrick.this);
		}
		
		@Override
		public void execute(IPlayerData playerData, SpellContext context) throws SpellRuntimeException {
			((SleepWhileTrick) piece).capture(context);
		}
		
	}
	
}
