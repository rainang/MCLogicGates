package com.github.rainang.logicgates;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public interface IDiode {

	boolean isPowered(IBlockState state);

	Block getPoweredBlock();

	Block getUnpoweredBlock();

	Block getBlock();

	IBlockState getPoweredState(IBlockState unpoweredState);

	IBlockState getUnpoweredState(IBlockState poweredState);

	EnumFacing getInput(IBlockState state);

	EnumFacing getOutput(IBlockState state);

	EnumSignal getInputSignal();

	EnumSignal getOutputSignal();
}
