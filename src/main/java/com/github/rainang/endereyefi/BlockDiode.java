package com.github.rainang.endereyefi;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockDiode extends Block {

	public final boolean isActive;

	public final int type;

	protected BlockDiode(Material material, boolean active, int type) {
		super(material);
		isActive = active;
		this.type = type;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		for(int i = 1; i < 16; i++) {
			BlockPos inputPos = pos.offset(side, i);
			IBlockState inputState = worldIn.getBlockState(inputPos);
			BlockDiode inputBlock = getAsDiode(inputState);
			boolean flag = false;
			if(inputBlock != null) {
				flag = inputBlock.isEnderReceiver() || !isEnderReceiver();
				if(inputBlock instanceof BlockCasterEye && isEnderReceiver())
					return 15;
				if(inputBlock.isActive && canReceiveSignalFrom(inputBlock) && isIOCompatible(inputState, state))
					return 15;
			} else if(i == 1 && !isEnderReceiver()) { // do vanilla calculation
				int power = worldIn.getRedstonePower(inputPos, side);
				if(power >= 15)
					return power;
				else {
					return Math.max(power, inputState.getBlock() == Blocks.redstone_wire ? (Integer)inputState
							.getValue(
							BlockRedstoneWire.POWER)                                     : 0);
				}
			}
			if(flag)
				break;
		}
		return 0;
	}

	protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
		if(isEnderTransmitter() || isEnderReceiver())
			for(EnumFacing facing : EnumFacing.values())
				for(int i = 1; i < 16; i++) {
					BlockPos offset = pos.offset(facing, i);
					BlockDiode block = getAsDiode(worldIn.getBlockState(offset));
					if(block != null && block.isEnderReceiver()) {
						worldIn.notifyBlockOfStateChange(offset, this);
						worldIn.notifyNeighborsOfStateChange(offset, this);
						break;
					}
				}
		EnumFacing facing = getOutput(state);
		BlockPos output = pos.offset(facing);
		worldIn.notifyBlockOfStateChange(output, this);
		worldIn.notifyNeighborsOfStateExcept(output, this, facing.getOpposite());
	}

	protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
		boolean flag = shouldBeActive(worldIn, pos, state);
		if((isActive && !flag || !isActive && flag) && !worldIn.isBlockTickPending(pos, this)) {
			byte b0 = -1;
			if(isFacingTowardsRepeater(worldIn, pos, state))
				b0 = -3;
			else if(isActive)
				b0 = -2;
			worldIn.updateBlockTick(pos, this, getTickDelay(state), b0);
		}
	}

	protected boolean isFacingTowardsRepeater(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing output = getOutput(state);
		BlockPos blockpos1 = pos.offset(output);
		return BlockRedstoneRepeater.isRedstoneRepeaterBlockID(worldIn.getBlockState(blockpos1).getBlock()) &&
				worldIn.getBlockState(blockpos1).getValue(BlockDirectional.FACING) != output;
	}

	protected boolean shouldBeActive(World worldIn, BlockPos pos, IBlockState state) {
		return calculateInputStrength(worldIn, pos, state, getInput(state)) > 0;
	}

	/** Checks if both are instances of BlockDiode and the transmitter output is opposite the receiver input */
	protected boolean isIOCompatible(IBlockState transmitter, IBlockState receiver) {
		BlockDiode dTransmitter = getAsDiode(transmitter);
		BlockDiode dReceiver = getAsDiode(receiver);
		return dTransmitter != null && dReceiver != null &&
				dTransmitter.getOutput(transmitter) == dReceiver.getInput(receiver).getOpposite();
	}

	protected boolean canReceiveSignalFrom(BlockDiode block) {
		return block.isEnderTransmitter() == isEnderReceiver();
	}

	/** Returns the state's block as BlockDiode if instance of BlockDiode, null otherwise */
	protected BlockDiode getAsDiode(IBlockState state) {
		return state.getBlock() instanceof BlockDiode ? (BlockDiode)state.getBlock() : null;
	}

	/** This block's signal transmission type */
	public boolean isEnderTransmitter() {
		return type >= 2;
	}

	/** This block's signal reception type */
	public boolean isEnderReceiver() {
		return type%2 == 1;
	}

	/** This block's signal transmission delay */
	public int getTickDelay(IBlockState state) {
		return 2;
	}

	/* Abstract methods */

	/** Retrieves the active state of the given state. Returns the state if is already active */
	public abstract IBlockState getActiveState(IBlockState state);

	/** Retrieves the passive state of the given state. Returns the state if is already passive */
	public abstract IBlockState getPassiveState(IBlockState state);

	/** Retrieves the state's input value */
	public abstract EnumFacing getInput(IBlockState state);

	/** Retrieves the state's output value */
	public abstract EnumFacing getOutput(IBlockState state);

	/* Block override */

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if(shouldBeActive(worldIn, pos, state))
			worldIn.setBlockState(pos, getActiveState(state));
		notifyNeighbors(worldIn, pos, state);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		notifyNeighbors(worldIn, pos, state);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		updateState(worldIn, pos, state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		boolean flag = shouldBeActive(worldIn, pos, state);
		if(isActive && !flag)
			worldIn.setBlockState(pos, getPassiveState(state), 2);
		else if(!isActive) {
			worldIn.setBlockState(pos, getActiveState(state), 2);
			if(!flag)
				worldIn.updateBlockTick(pos, getActiveState(state).getBlock(), getTickDelay(state), -1);
		}
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return isProvidingWeakPower(worldIn, pos, state, side);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return isActive && !isEnderTransmitter() && getOutput(state) == side.getOpposite() ? 15 : 0;
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(isActive)
			Blocks.ender_chest.randomDisplayTick(worldIn, pos, state, rand);
	}
}