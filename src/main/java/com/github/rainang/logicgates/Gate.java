package com.github.rainang.logicgates;

import java.util.function.Predicate;
import net.minecraft.util.IStringSerializable;

public enum Gate implements IStringSerializable {
	BUFFER(new Predicate<int[]>() {
		@Override
		public boolean test(int[] ai) {
			return (ai[1]&1) == 1;
		}
	}),
	NOT(BUFFER.predicate.negate()),
	AND(new Predicate<int[]>() {
		@Override
		public boolean test(int[] ai) {
			for(int i = 0; i < ai[0]; i++) {
				int bit = 1<<i;
				if((ai[1]&bit) != bit)
					return false;
			}
			return true;
		}
	}),
	OR(new Predicate<int[]>() {
		@Override
		public boolean test(int[] ai) {
			for(int i = 0; i < ai[0]; i++) {
				int bit = 1<<i;
				if((ai[1]&bit) == bit)
					return true;
			}
			return false;
		}
	}),
	XOR(new Predicate<int[]>() {
		@Override
		public boolean test(int[] ai) {
			boolean b = false;
			for(int i = 0; i < ai[0]; i++) {
				int bit = 1<<i;
				if((ai[1]&bit) == bit) {
					if(b)
						return false;
					else
						b = true;
				}
			}
			return b;
		}
	}),
	NAND(AND.predicate.negate()),
	NOR(OR.predicate.negate()),
	XNOR(XOR.predicate.negate());

	private final Predicate<int[]> predicate;

	Gate(Predicate<int[]> predicate) {
		this.predicate = predicate;
	}

	public boolean validate(int inputs, int state) {
		return predicate.test(new int[] { inputs, state });
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}
}
