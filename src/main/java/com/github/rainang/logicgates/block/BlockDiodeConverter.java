package com.github.rainang.logicgates.block;

import com.github.rainang.logicgates.diode.Gate;
import com.github.rainang.logicgates.diode.Signal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public abstract class BlockDiodeConverter extends BlockDiode1In {

	public BlockDiodeConverter(Gate gate, int type) {
		super(gate, type);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return getSignal(state) == Signal.ENDER && isActive(state) && getOutput(state).getOpposite() == side ? 15 : 0;
	}
}
