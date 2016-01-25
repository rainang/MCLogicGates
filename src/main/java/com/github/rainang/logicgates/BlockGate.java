package com.github.rainang.logicgates;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockGate extends BlockDiode {

	public static final PropertyInteger INPUT = PropertyInteger.create("input", 0, 3);

	protected final EnumSignal signal;
	protected final EnumGate   gate;

	public BlockGate(EnumSignal enumSignal, EnumGate enumGate) {
		super(enumSignal.name().toLowerCase() + "_" + enumGate.name().toLowerCase() + "_gate", false);
		signal = enumSignal;
		gate = enumGate;
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(INPUT, 0));
	}

	@Override
	public boolean isPowered(IBlockState state) {
		int i = (Integer)state.getValue(INPUT);
		return gate.execute((i|1) == i, (i|2) == i);
	}

	@Override
	public Block getPoweredBlock() {
		return this;
	}

	@Override
	public Block getUnpoweredBlock() {
		return this;
	}

	@Override
	public IBlockState getPoweredState(IBlockState unpoweredState) {
		return unpoweredState;
	}

	@Override
	public IBlockState getUnpoweredState(IBlockState poweredState) {
		return poweredState;
	}

	@Override
	public EnumFacing getInput(IBlockState state) {
		return getOutput(state).getOpposite();
	}

	@Override
	public EnumFacing getOutput(IBlockState state) {
		return (EnumFacing)state.getValue(FACING);
	}

	@Override
	public EnumSignal getInputSignal() {
		return signal;
	}

	@Override
	public EnumSignal getOutputSignal() {
		return signal;
	}

	@Override
	public IBlockState onBlockPlaced(
			World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing facing = (EnumFacing)state.getValue(FACING);
		int l = signal.calculateInputStrength(worldIn, pos, facing.rotateYCCW()) > 0 ? 1 : 0;
		int r = signal.calculateInputStrength(worldIn, pos, facing.rotateY()) > 0 ? 2 : 0;
		return l|r;
	}

	/* BLOCK OVERRIDE */

	@Override
	public boolean onBlockActivated(
			World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(!playerIn.capabilities.allowEdit)
			return false;
		else {
			worldIn.setBlockState(pos, LogicGates.gates[getInputSignal().ordinal()][(gate.ordinal() + 1)%
					EnumGate.values().length].getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
			return true;
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		Integer current = (Integer)state.getValue(INPUT);
		int b = calculateInputStrength(worldIn, pos, state);
		if(current != b) {
			IBlockState newState = getDefaultState().withProperty(FACING, state.getValue(FACING))
					.withProperty(INPUT, b);
			worldIn.setBlockState(pos, newState, 2);
			worldIn.updateBlockTick(pos, newState.getBlock(), 2, -1);
			notifyNeighbors(worldIn, pos, newState);
		}
	}

	@Override
	protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
		boolean flag = shouldBePowered(worldIn, pos, state);
		if(flag && !worldIn.isBlockTickPending(pos, this)) {
			byte b0 = (byte)(isPowered ? -2 : -1);
			worldIn.updateBlockTick(pos, this, getTickDelay(state), b0);
		}
	}

	@Override
	protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
		return calculateInputStrength(worldIn, pos, state) != (Integer)state.getValue(INPUT);
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, FACING, INPUT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(INPUT, meta>>2);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		byte b0 = 0;
		int i = b0|((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
		i |= (Integer)state.getValue(INPUT)<<2;
		return i;
	}
}