package com.github.rainang.logicgates;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDirector extends BlockDiode {

	public static final PropertyInteger OUT = PropertyInteger.create("out", 0, 1);

	private final EnumSignal signal;

	protected BlockDirector(EnumSignal signal, boolean powered) {
		super(signal.name().toLowerCase() + "_director", powered);
		this.signal = signal;
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OUT, 0));
	}

	@Override
	public boolean onBlockActivated(
			World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(!playerIn.capabilities.allowEdit) {
			return false;
		} else {
			worldIn.setBlockState(pos, state.cycleProperty(OUT), 3);
			return true;
		}
	}

	@Override
	public Block getPoweredBlock() {
		return LogicGates.directors[1][signal.ordinal()];
	}

	@Override
	public Block getUnpoweredBlock() {
		return LogicGates.directors[0][signal.ordinal()];
	}

	@Override
	public EnumFacing getInput(IBlockState state) {
		return (EnumFacing)state.getValue(FACING);
	}

	@Override
	public EnumFacing getOutput(IBlockState state) {
		EnumFacing in = getInput(state);
		int out = (Integer)state.getValue(OUT);
		return out == 0 ? in.rotateY() : in.rotateYCCW();
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
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(OUT, (meta>>2));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		byte b0 = 0;
		int i = b0|((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
		i |= (Integer)state.getValue(OUT)<<2;
		return i;
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, FACING, OUT);
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!isPowered)
			return;
		EnumFacing enumfacing = ((EnumFacing)state.getValue(FACING)).rotateY();
		double d0 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
		double d1 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
		double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
		float f = 4;

		if((Integer)state.getValue(OUT) == 1)
			f = -4;

		f /= 16.0F;
		double d3 = (double)(f*(float)enumfacing.getFrontOffsetX());
		double d4 = (double)(f*(float)enumfacing.getFrontOffsetZ());
		worldIn.spawnParticle(signal.particleType, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
	}
}
