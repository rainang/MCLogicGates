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

	public final boolean isPowered;

	protected BlockDiode(Material material, boolean powered) {
		super(material);
		isPowered = powered;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		for(int i = 1; i < 16; i++) {
			IBlockState inputState = worldIn.getBlockState(pos.offset(side, i));
			BlockDiode inputBlock = getAsDiode(inputState);
			boolean flag = false;
			if(inputBlock != null) {
				flag = inputBlock.isEnderReceiver() || !isEnderReceiver();
				if(inputBlock instanceof BlockCasterEye && isEnderReceiver())
					return 15;
				if(inputBlock.isPowered && isTypeCompatible(inputBlock) && isIOCompatible(inputState, state))
					return 15;
			} else if(i == 1 && !isEnderReceiver()) { // do vanilla calculation
				BlockPos blockpos1 = pos.offset(side);
				int power = worldIn.getRedstonePower(blockpos1, side);
				if(power >= 15)
					return power;
				else {
					IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
					return Math.max(power,
							iblockstate1.getBlock() == Blocks.redstone_wire ? (Integer)iblockstate1.getValue(
									BlockRedstoneWire.POWER) : 0);
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
				for(int i = 0; i < 16; i++) {
					BlockPos offset = pos.offset(facing, i + 1);
					IBlockState offsetState = worldIn.getBlockState(offset);
					BlockDiode block = getAsDiode(offsetState);
					if(block != null && block.isEnderReceiver()) {
						worldIn.notifyBlockOfStateChange(offset, this);
						worldIn.notifyNeighborsOfStateChange(offset, this);
						break;
					}
				}
		EnumFacing facing = getOutputSide(state);
		BlockPos output = pos.offset(facing);
		worldIn.notifyBlockOfStateChange(output, this);
		worldIn.notifyNeighborsOfStateExcept(output, this, facing.getOpposite());
	}

	protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
		boolean flag = shouldBePowered(worldIn, pos, state);
		if((isPowered && !flag || !isPowered && flag) && !worldIn.isBlockTickPending(pos, this)) {
			byte b0 = -1;
			if(isFacingTowardsRepeater(worldIn, pos, state))
				b0 = -3;
			else if(isPowered)
				b0 = -2;
			worldIn.updateBlockTick(pos, this, getTickDelay(state), b0);
		}
	}

	protected boolean isFacingTowardsRepeater(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing output = getOutputSide(state);
		BlockPos blockpos1 = pos.offset(output);
		return BlockRedstoneRepeater.isRedstoneRepeaterBlockID(worldIn.getBlockState(blockpos1).getBlock()) &&
				worldIn.getBlockState(blockpos1).getValue(BlockDirectional.FACING) != output;
	}

	protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
		return calculateInputStrength(worldIn, pos, state, getInputSide(state)) > 0;
	}

	protected boolean isIOCompatible(IBlockState transmitter, IBlockState receiver) {
		BlockDiode dTransmitter = getAsDiode(transmitter);
		BlockDiode dReceiver = getAsDiode(receiver);
		return dTransmitter != null && dReceiver != null &&
				dTransmitter.getOutputSide(transmitter) == dReceiver.getInputSide(receiver).getOpposite();
	}

	protected boolean isTypeCompatible(BlockDiode block) {
		return block.isEnderTransmitter() == isEnderReceiver();
	}

	protected BlockDiode getAsDiode(IBlockState state) {
		return state.getBlock() instanceof BlockDiode ? (BlockDiode)state.getBlock() : null;
	}

	/* Abstract methods */

	public abstract int getTickDelay(IBlockState state);

	public abstract boolean isEnderTransmitter();

	public abstract boolean isEnderReceiver();

	public abstract IBlockState getPoweredState(IBlockState state);

	public abstract IBlockState getUnpoweredState(IBlockState state);

	public abstract EnumFacing getInputSide(IBlockState state);

	public abstract EnumFacing getOutputSide(IBlockState state);

	/* Block override */

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if(calculateInputStrength(worldIn, pos, state, getInputSide(state)) > 0)
			worldIn.setBlockState(pos, getPoweredState(state));
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
		boolean flag = shouldBePowered(worldIn, pos, state);
		if(isPowered && !flag)
			worldIn.setBlockState(pos, getUnpoweredState(state), 2);
		else if(!isPowered) {
			worldIn.setBlockState(pos, getPoweredState(state), 2);
			if(!flag)
				worldIn.updateBlockTick(pos, getPoweredState(state).getBlock(), getTickDelay(state), -1);
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
		return isPowered && !isEnderTransmitter() && getOutputSide(state) == side.getOpposite() ? 15 : 0;
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(isPowered)
			Blocks.ender_chest.randomDisplayTick(worldIn, pos, state, rand);
	}
}