package com.github.rainang.logicgates.diode;

import com.github.rainang.logicgates.LogicGates;
import com.github.rainang.logicgates.block.*;
import net.minecraft.block.state.IBlockState;

public class DiodeFactory {

	public static BlockDiode[] create1InputDiode(String name, Gate gate) {
		final BlockDiode[] diodes = new BlockDiode[3];
		for(int i = 0; i < 3; i++) {
			final int j = (i + 1)%3;
			diodes[i] = new BlockDiode1In(gate, i) {
				@Override
				public BlockDiode getBaseBlock() {
					return diodes[0];
				}

				@Override
				public IBlockState rotate(IBlockState state) {
					return diodes[j].getDefaultState()
							.withProperty(OUT, state.getValue(OUT))
							.withProperty(INPUT, state.getValue(INPUT))
							.withProperty(SIGNAL, state.getValue(SIGNAL));
				}
			};
			diodes[i].setUnlocalizedName(name + "_" + i);
			if(i == 0)
				diodes[i].setCreativeTab(LogicGates.TAB_GATES);
		}
		return diodes;
	}

	public static BlockDiode[] createConverterDiode() {
		final BlockDiode[] diodes = new BlockDiode[3];
		for(int i = 0; i < 3; i++) {
			final int j = (i + 1)%3;
			diodes[i] = new BlockDiodeConverter(Gate.BUFFER, i) {
				@Override
				public BlockDiode getBaseBlock() {
					return diodes[0];
				}

				@Override
				public IBlockState rotate(IBlockState state) {
					return diodes[j].getDefaultState()
							.withProperty(OUT, state.getValue(OUT))
							.withProperty(INPUT, state.getValue(INPUT))
							.withProperty(SIGNAL, state.getValue(SIGNAL));
				}
			};
			diodes[i].setUnlocalizedName("converter_" + i);
			if(i == 0)
				diodes[i].setCreativeTab(LogicGates.TAB_GATES);
		}
		return diodes;
	}

	public static BlockDiode[] create2InputDiode(String name, Gate gate) {
		final BlockDiode[] diodes = new BlockDiode[6];
		for(final Signal signal : Signal.values())
			for(int i = 0, j = signal.ordinal()*3 + i; i < 3; i++, j = signal.ordinal()*3 + i) {
				final int k = signal.ordinal()*3 + (i + 1)%3;
				diodes[j] = new BlockDiode2In(signal, gate, i) {
					@Override
					public BlockDiode getBaseBlock() {
						return diodes[signal.ordinal()*3];
					}

					@Override
					public IBlockState rotate(IBlockState state) {
						return diodes[k].getDefaultState()
								.withProperty(OUT, state.getValue(OUT))
								.withProperty(INPUT, state.getValue(INPUT));
					}
				};
				diodes[j].setUnlocalizedName((signal == Signal.ENDER ? signal.getName() + "_" : "") + "diode2in_" +
						name + "_" + i);
				if(i == 0)
					diodes[j].setCreativeTab(LogicGates.TAB_GATES);
			}
		return diodes;
	}

	public static BlockDiode[] create3InputDiode(String name, Gate gate) {
		final BlockDiode[] diodes = new BlockDiode[4];
		for(final Signal signal : Signal.values())
			for(int i = 0; i < 2; i++) {
				final int j = signal.ordinal()*2 + i;
				if(i == 0)
					diodes[j] = new BlockDiode3In(signal, gate, i) {
						@Override
						public BlockDiode getBaseBlock() {
							return diodes[signal.ordinal()*2];
						}

						@Override
						public IBlockState setInputState(IBlockState state, int input) {
							if(input < 4)
								return state.withProperty(getInputProperty(), input);
							return diodes[j + 1].getDefaultState()
									.withProperty(BlockDiode.OUT, state.getValue(OUT))
									.withProperty(getInputProperty(), input%4);
						}
					};
				else
					diodes[j] = new BlockDiode3In(signal, gate, i) {
						@Override
						public BlockDiode getBaseBlock() {
							return diodes[signal.ordinal()*2];
						}

						@Override
						public IBlockState setInputState(IBlockState state, int input) {
							if(input >= 4)
								return state.withProperty(getInputProperty(), input%4);
							return diodes[j - 1].getDefaultState()
									.withProperty(BlockDiode.OUT, state.getValue(OUT))
									.withProperty(getInputProperty(), input);
						}
					};
				diodes[j].setUnlocalizedName((signal == Signal.ENDER ? signal.getName() + "_" : "") + "diode3in_" +
						name + "_" + i);
				if(i == 0)
					diodes[j].setCreativeTab(LogicGates.TAB_GATES);
			}
		return diodes;
	}

	public static BlockDiode[] create5InputDiode() {
		final BlockDiode[] diodes = new BlockDiode[4];
		for(final Signal signal : Signal.values())
			for(int i = 0; i < 2; i++) {
				final int j = signal.ordinal()*2 + i;
				if(i == 0)
					diodes[j] = new BlockDiode5In(signal, false) {
						@Override
						public BlockDiode getBaseBlock() {
							return diodes[signal.ordinal()*2];
						}

						@Override
						public IBlockState rotate(IBlockState state) {
							return diodes[j + 1].getDefaultState().withProperty(INPUT, state.getValue(INPUT));
						}
					};
				else
					diodes[j] = new BlockDiode5In(signal, true) {
						@Override
						public BlockDiode getBaseBlock() {
							return diodes[signal.ordinal()*2];
						}

						@Override
						public IBlockState rotate(IBlockState state) {
							return diodes[j - 1].getDefaultState().withProperty(INPUT, state.getValue(INPUT));
						}
					};
				diodes[j].setUnlocalizedName((signal == Signal.ENDER ? signal.getName() + "_" : "") + "diode5in_" + i);
				if(i == 0)
					diodes[j].setCreativeTab(LogicGates.TAB_GATES);
			}
		return diodes;
	}
}
