package com.github.rainang.logicgates.block;

import com.github.rainang.logicgates.diode.Gate;
import com.github.rainang.logicgates.diode.Signal;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public abstract class BlockDiode2In extends BlockDiode {

	public static final PropertyInteger INPUT = PropertyInteger.create("input", 0, 3);

	private final Signal signal;

	public final int type;

	public BlockDiode2In(Signal signal, Gate gate, int type) {
		super(gate);
		this.signal = signal;
		this.type = type;
		setDefaultState(
				blockState.getBaseState().withProperty(OUT, EnumFacing.NORTH).withProperty(getInputProperty(), 0));
	}

	@Override
	public Signal getSignal(IBlockState state) {
		return signal;
	}

	@Override
	public int getInputCount() {
		return 2;
	}

	@Override
	public PropertyInteger getInputProperty() {
		return INPUT;
	}

	@Override
	public int getInputState(IBlockState state) {
		return (Integer)state.getValue(INPUT);
	}

	@Override
	public EnumFacing getInput(IBlockState state, int index) {
		EnumFacing out = getOutput(state);
		switch(type) {
		default:
		case 0:
			return index == 0 ? out.rotateYCCW() : out.rotateY();
		case 1:
			return index == 0 ? out.rotateYCCW() : out.getOpposite();
		case 2:
			return index == 0 ? out.rotateY() : out.getOpposite();
		}
	}

	@Override
	public List<EnumFacing> getInputs(IBlockState state) {
		return Arrays.asList(getInput(state, 0), getInput(state, 1));
	}
}
