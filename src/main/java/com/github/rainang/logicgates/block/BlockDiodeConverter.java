package com.github.rainang.logicgates.block;

import com.github.rainang.logicgates.Gate;
import com.github.rainang.logicgates.Signal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockDiodeConverter extends BlockDiode1In {

	public BlockDiodeConverter(Gate gate, int type) {
		super(gate, type);
	}

	@Override
	protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
		notifyEnderNeighbors(worldIn, pos);
		if(getSignal(state) == Signal.REDSTONE)
			notifyRedstoneNeighbors(worldIn, pos, state);
	}

	@Override
	protected EnumParticleTypes getParticleType(IBlockState state) {
		return getSignal(state) == Signal.REDSTONE ? EnumParticleTypes.PORTAL : EnumParticleTypes.REDSTONE;
	}

	/* BLOCK OVERRIDE */

	@Override
	public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return getSignal(state) == Signal.ENDER && isActive(state) && getOutput(state).getOpposite() == side ? 15 : 0;
	}
}
