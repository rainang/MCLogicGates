package com.github.rainang.logicgates;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVerticalRepeater extends BlockDiode {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.VERTICAL);

	private final EnumSignal signal;

	protected BlockVerticalRepeater(EnumSignal signal, boolean powered) {
		super(signal.name().toLowerCase() + "_vertical_repeater", powered);
		this.signal = signal;
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN));
	}

	protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
		int power;
		EnumFacing out = getOutput(state);
		EnumSignal signal = getInputSignal();
		for(EnumFacing facing : EnumFacing.VALUES) {
			if(facing == out)
				continue;
			if(signal == EnumSignal.REDSTONE)
				power = signal.calculateInputStrength(worldIn, pos, facing);
			else
				power = signal.getPowerFromSide(worldIn, pos, facing);
			if(power > 0)
				return power;
		}
		return 0;
	}

	protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
		if(signal == EnumSignal.REDSTONE)
			for(EnumFacing facing : EnumFacing.Plane.VERTICAL) {
				worldIn.notifyBlockOfStateChange(pos.offset(facing), state.getBlock());
				worldIn.notifyNeighborsOfStateExcept(pos.offset(facing), state.getBlock(), facing.getOpposite());
			}
		else
			super.notifyNeighbors(worldIn, pos, state);
	}

	public boolean onBlockActivated(
			World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(!playerIn.capabilities.allowEdit)
			return false;
		state = state.cycleProperty(FACING);
		if(shouldBePowered(worldIn, pos, state))
			state = getPoweredState(state);
		else
			state = getUnpoweredState(state);
		worldIn.setBlockState(pos, state);
		notifyNeighbors(worldIn, pos, state);
		return true;
	}

	public IBlockState onBlockPlaced(
			World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, EnumFacing.DOWN);
	}

	@Override
	public Block getPoweredBlock() {
		return LogicGates.verticals[1][getInputSignal().ordinal()];
	}

	@Override
	public Block getUnpoweredBlock() {
		return LogicGates.verticals[0][getInputSignal().ordinal()];
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
		return signal;
	}

	@Override
	public EnumSignal getOutputSignal() {
		return signal;
	}

	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, meta == 0 ? EnumFacing.UP : EnumFacing.DOWN);
	}

	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing)state.getValue(FACING)).getAxisDirection().ordinal();
	}

	protected BlockState createBlockState() {
		return new BlockState(this, FACING);
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!isPowered)
			return;
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
		double d0 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
		double d1 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
		double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
		float f = 2;

		f /= 16.0F;
		double d3 = (double)(f*(float)enumfacing.getFrontOffsetX());
		double d4 = (double)(f*(float)enumfacing.getFrontOffsetZ());
		worldIn.spawnParticle(signal.particleType, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
	}
}