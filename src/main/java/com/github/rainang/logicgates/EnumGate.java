package com.github.rainang.logicgates;

public enum EnumGate {
	AND(new ILogic() {
		@Override
		public boolean execute(boolean x, boolean y) {
			return x && y;
		}
	}),
	NAND(new ILogic() {
		@Override
		public boolean execute(boolean x, boolean y) {
			return !AND.logic.execute(x, y);
		}
	}),
	OR(new ILogic() {
		@Override
		public boolean execute(boolean x, boolean y) {
			return x || y;
		}
	}),
	NOR(new ILogic() {
		@Override
		public boolean execute(boolean x, boolean y) {
			return !OR.execute(x, y);
		}
	}),
	XOR(new ILogic() {
		@Override
		public boolean execute(boolean x, boolean y) {
			return x^y;
		}
	}),
	XNOR(new ILogic() {
		@Override
		public boolean execute(boolean x, boolean y) {
			return !XOR.execute(x, y);
		}
	});

	private final ILogic logic;

	EnumGate(ILogic logic) {
		this.logic = logic;
	}

	public boolean execute(boolean x, boolean y) {
		return logic.execute(x, y);
	}

	public interface ILogic {

		boolean execute(boolean x, boolean y);
	}
}
