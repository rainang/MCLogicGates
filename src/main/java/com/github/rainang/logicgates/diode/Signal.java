package com.github.rainang.logicgates.diode;

import net.minecraft.util.IStringSerializable;

public enum Signal implements IStringSerializable {
	REDSTONE(2), ENDER(16);

	public final int range;

	Signal(int range) {
		this.range = range;
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}
}
