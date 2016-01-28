package com.github.rainang.logicgates.diode;

import com.github.rainang.logicgates.block.BlockDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class DiodeConnection {

	public final int distance;

	public final BlockDiode src;
	public final BlockDiode connect;

	public final BlockPos srcPos;
	public final BlockPos connectPos;

	public final IBlockState srcState;
	public final IBlockState connectState;

	public DiodeConnection(
			BlockDiode src, BlockDiode connect, BlockPos srcPos, BlockPos connectPos, IBlockState srcState,
			IBlockState connectState, int distance) {
		this.src = src;
		this.connect = connect;
		this.srcPos = srcPos;
		this.connectPos = connectPos;
		this.distance = distance;
		this.srcState = srcState;
		this.connectState = connectState;
	}

	public boolean hasActiveConnection() {
		return connect.isActive(connectState);
	}
}
