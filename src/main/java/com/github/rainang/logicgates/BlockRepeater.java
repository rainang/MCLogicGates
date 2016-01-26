package com.github.rainang.logicgates;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRepeater extends BlockDiode implements IDiode {

	public static final PropertyBool    LOCKED = PropertyBool.create("locked");
	public static final PropertyInteger DELAY  = PropertyInteger.create("delay", 1, 4);

	private final EnumSignal input;
	private final EnumSignal output;

	protected BlockRepeater(EnumSignal input, EnumSignal output, boolean powered) {
		super(input.name().toLowerCase() + (input == output ? "_repeater" : "_converter"), powered);
		this.input = input;
		this.output = output;
		setDefaultState(blockState.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(DELAY, 1)
				.withProperty(LOCKED, false));
	}

	protected int getPowerOnSides(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
		EnumFacing input = (EnumFacing)state.getValue(FACING);
		int l = getInputSignal().getPowerFromSideWithCheck(worldIn, pos, input.rotateY());
		int r = getInputSignal().getPowerFromSideWithCheck(worldIn, pos, input.rotateYCCW());
		return Math.max(l, r);
	}

	public boolean isFacingTowardsRepeater(World worldIn, BlockPos pos) {
		return getOutputSignal().getNearestReceiver(worldIn, pos, this) != null;
	}

	@Override
	protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
		if(!isLocked(worldIn, pos, state)) {
			boolean flag = shouldBePowered(worldIn, pos, state);
			if((isPowered && !flag || !isPowered && flag) && !worldIn.isBlockTickPending(pos, this)) {
				byte b0 = -1;
				if(isFacingTowardsRepeater(worldIn, pos))
					b0 = -3;
				else if(isPowered)
					b0 = -2;
				worldIn.updateBlockTick(pos, this, getTickDelay(state), b0);
			}
		}
	}

	@Override
	protected int getTickDelay(IBlockState state) {
		return (Integer)state.getValue(DELAY)*2;
	}

	@Override
	public Block getPoweredBlock() {
		return LogicGates.repeaters[1][getInputSignal().ordinal()][getOutputSignal().ordinal()];
	}

	@Override
	public Block getUnpoweredBlock() {
		return LogicGates.repeaters[0][getInputSignal().ordinal()][getOutputSignal().ordinal()];
	}

	public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
		return getPowerOnSides(worldIn, pos, state) > 0;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(LOCKED, isLocked(worldIn, pos, state));
	}

	@Override
	public EnumFacing getInput(IBlockState state) {
		return (EnumFacing)state.getValue(FACING);
	}

	@Override
	public EnumFacing getOutput(IBlockState state) {
		return getInput(state).getOpposite();
	}

	@Override
	public EnumSignal getInputSignal() {
		return input;
	}

	@Override
	public EnumSignal getOutputSignal() {
		return output;
	}

	/* BLOCK OVERRIDE */

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!isLocked(worldIn, pos, state))
			super.updateTick(worldIn, pos, state, rand);
	}

	@Override
	public boolean onBlockActivated(
			World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(!playerIn.capabilities.allowEdit)
			return false;
		else {
			worldIn.setBlockState(pos, state.cycleProperty(DELAY), 3);
			return true;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(isPowered) {
			EnumParticleTypes particleType = getOutputSignal().particleType;
			EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
			double d0 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
			double d1 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
			double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
			float f = -5.0F;

			if(rand.nextBoolean()) {
				particleType = getInputSignal().particleType;
				f = (float)((Integer)state.getValue(DELAY)*2 - 1);
			}

			f /= 16.0F;
			double d3 = (double)(f*(float)enumfacing.getFrontOffsetX());
			double d4 = (double)(f*(float)enumfacing.getFrontOffsetZ());
			worldIn.spawnParticle(particleType, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta))
				.withProperty(LOCKED, false)
				.withProperty(DELAY, 1 + (meta>>2));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		byte b0 = 0;
		int i = b0|((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
		i |= (Integer)state.getValue(DELAY) - 1<<2;
		return i;
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, FACING, DELAY, LOCKED);
	}
}