package com.github.rainang.logicgates.block;

import com.github.rainang.logicgates.Gate;
import com.github.rainang.logicgates.Signal;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BlockDiode3In extends BlockDiode2In {

	public static final PropertyInteger INPUT = PropertyInteger.create("input", 0, 3);

	public BlockDiode3In(Signal signal, Gate gate, int type) {
		super(signal, gate, type);
		setDefaultState(
				blockState.getBaseState().withProperty(OUT, EnumFacing.NORTH).withProperty(getInputProperty(), 0));
	}

	@Override
	public PropertyInteger getInputProperty() {
		return INPUT;
	}

	@Override
	public EnumFacing getInput(IBlockState state, int index) {
		EnumFacing out = getOutput(state);
		return index == 0 ? out.rotateYCCW() : index == 1 ? out.rotateY() : out.getOpposite();
	}

	@Override
	public List<EnumFacing> getInputs(IBlockState state) {
		return Arrays.asList(getInput(state, 0), getInput(state, 1), getInput(state, 2));
	}

	@Override
	public int getInputState(IBlockState state) {
		int i = state.getValue(INPUT);
		return type == 0 ? i : i + 4;
	}

	@Override
	public int getInputCount() {
		return 3;
	}

	@Override
	public IBlockState rotate(IBlockState state) {
		return state;
	}

	@Override
	public abstract IBlockState setInputState(IBlockState state, int input);

	/* BLOCK OVERRIDE */

	@Override
	public boolean onBlockActivated(
			World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		return false;
	}
}
